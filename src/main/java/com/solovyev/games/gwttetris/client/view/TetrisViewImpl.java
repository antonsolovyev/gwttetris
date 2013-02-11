package com.solovyev.games.gwttetris.client.view;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.solovyev.games.tetris.*;

import java.util.HashMap;
import java.util.Map;


public class TetrisViewImpl  implements TetrisView
{
    private static final int FIELD_PADDING = 5;
    private static final int FIELD_BORDER = 2;
    private static final int TEXT_PADDING = 1;
    private static final int MAX_TEXT_SIZE = 12;
    private static final int PREVIEW_WIDTH = 5;
    private static final int PREVIEW_HEIGHT = 5;
    private static final int MAX_CELL_SIZE = 23;
    private static final String GRID_COLOR = "#404040";
    private static final String FIELD_COLOR = "black";
    private static final String BACKGROUND_COLOR = "gray";
    private static final Map<String, String> COLOR_TO_IMAGE = new HashMap<String, String>()
    {{
            put(Cell.Color.CYAN.name(), "cell_cyan.png");
            put(Cell.Color.BLUE.name(), "cell_blue.png");
            put(Cell.Color.ORANGE.name(), "cell_orange.png");
            put(Cell.Color.YELLOW.name(), "cell_yellow.png");
            put(Cell.Color.GREEN.name(), "cell_green.png");
            put(Cell.Color.PURPLE.name(), "cell_magenta.png");
            put(Cell.Color.RED.name(), "cell_red.png");
    }};
    
    private FlexTable flexTable;
    private Canvas gameCanvas;
    private Canvas previewCanvas;
    private Label scoreLabel;
    private Label speedLabel;
    private Label linesLabel;
    private Label piecesLabel;

    private Presenter presenter;
    private TetrisEngine tetrisEngine;

    private int cellSize;
    private boolean isGridShown = false;
    private boolean isPreviewShown = true;


    public TetrisViewImpl()
    {
        if(!Canvas.isSupported())
        {
            throw new RuntimeException("Browser does not support HTML5 canvas!");
        }
        
        flexTable = new FlexTable();

        previewCanvas = Canvas.createIfSupported();
        flexTable.setWidget(0, 0, previewCanvas);

        gameCanvas = Canvas.createIfSupported();
        flexTable.setWidget(0, 1, gameCanvas);

        Panel scorePanel = makeScorePanel();
        flexTable.setWidget(1, 0, scorePanel);

        Panel controlPanel = makeControlPanel();
        flexTable.setWidget(0, 2, controlPanel);
        
        flexTable.getFlexCellFormatter().setRowSpan(0, 1, 2);
        flexTable.getFlexCellFormatter().setRowSpan(0, 2, 2);
        flexTable.getElement().getStyle().setBackgroundColor(BACKGROUND_COLOR);

        initInputHandling();
    }

    private Panel makeScorePanel()
    {
        VerticalPanel scorePanel = new VerticalPanel();
        scorePanel.getElement().getStyle().setBackgroundColor("red");
        scorePanel.setBorderWidth(10);
        scorePanel.getElement().getStyle().setBorderColor("blue");
        scoreLabel = new Label("Score: ");
        scorePanel.add(scoreLabel);
        speedLabel = new Label("Speed: ");
        scorePanel.add(speedLabel);
        linesLabel = new Label("Lines: ");
        scorePanel.add(linesLabel);
        piecesLabel = new Label("Pieces: ");
        scorePanel.add(piecesLabel);

        return scorePanel;
    }
    
    private Panel makeControlPanel()
    {
        VerticalPanel controlPanel = new VerticalPanel();
        controlPanel.getElement().getStyle().setBackgroundColor("blue");
        controlPanel.setBorderWidth(10);
        controlPanel.getElement().getStyle().setBorderColor("red");

        final Button startButton = new Button("New game");
        startButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                tetrisEngine.stop();
                tetrisEngine.start();
            }
        });
        controlPanel.add(startButton);

        final Button pauseButton = new Button("Pause");
        pauseButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                if(tetrisEngine.getGameState() == TetrisEngine.GameState.PAUSED)
                {
                    pauseButton.setText("Pause");
                    tetrisEngine.resume();
                }
                else if(tetrisEngine.getGameState() == TetrisEngine.GameState.RUNNING)
                {
                    pauseButton.setText("Resume");
                    tetrisEngine.pause();
                }
            }
        });
        controlPanel.add(pauseButton);

        final CheckBox previewCheckBox = new CheckBox("Show preview");
        previewCheckBox.setValue(true);
        previewCheckBox.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                if(previewCheckBox.getValue())
                {
                    isPreviewShown = true;
                }
                else
                {
                    isPreviewShown = false;
                }
                refresh();
            }
        });
        controlPanel.add(previewCheckBox);

        final CheckBox gridCheckBox = new CheckBox("Show grid");
        gridCheckBox.setValue(false);
        gridCheckBox.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                if(gridCheckBox.getValue())
                {
                    isGridShown = true;
                }
                else
                {
                    isGridShown = false;
                }
                refresh();
            }
        });
        controlPanel.add(gridCheckBox);
        
        return controlPanel;
    }
    
    @Override
    public void setPresenter(Presenter presenter)
    {
        this.presenter = presenter;
        this.tetrisEngine = presenter.getTetrisEngine();

        initCellSize();
        initGameCanvasSize();
        initPreviewCanvasSize();
    }

    @Override
    public Widget asWidget()
    {
        return flexTable;
    }

    private static class Point
    {
        int x;
        int y;

        public Point(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }

    private static class Rectangle
    {
        int top;
        int right;
        int bottom;
        int left;

        public int getHeight()
        {
            return bottom - top;
        }

        public int getWidth()
        {
            return right - left;
        }

        public Rectangle(int left, int top, int right, int bottom)
        {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

    private int getWindowWidth()
    {
        return Window.getClientWidth();
    }

    private int getWindowHeight()
    {
        return Window.getClientHeight();
    }

    private int getCellSize()
    {
        int width = getWindowWidth();
        int height = getWindowHeight();
        double res = Math.min(
                height / tetrisEngine.getHeight(),
                width / (tetrisEngine.getWidth() + PREVIEW_WIDTH));
        if(res > MAX_CELL_SIZE)
        {
            res = MAX_CELL_SIZE;
        }

        return (int) Math.round(res);
    };

    private Rectangle getCellRectangle(Cell cell, Point offset)
    {
        int left = offset.x + cell.getX() * cellSize;
        int top = offset.y + cell.getY() * cellSize;
        int right = left + cellSize;
        int bottom = top + cellSize;

        return new Rectangle(left, top, right, bottom);
    };

    private int getGlassHeight()
    {
        return tetrisEngine.getHeight() * cellSize;
    }

    private int getGlassWidth()
    {
        return tetrisEngine.getWidth() * cellSize;
    }

    private int getPreviewHeight()
    {
        return PREVIEW_HEIGHT * cellSize;
    }

    private int getPreviewWidth()
    {
        return PREVIEW_WIDTH * cellSize;
    }

    private Point getPreviewCellOffset()
    {
        int minX = PREVIEW_WIDTH;
        int maxX = 0;
        int minY = PREVIEW_HEIGHT;
        int maxY = 0;
        for(Cell c : tetrisEngine.getNextPiece().getCells())
        {
            if(c.getX() > maxX)
            {
                maxX = c.getX();
            }
            if(c.getX() < minX)
            {
                minX = c.getX();
            }
            if(c.getY() > maxY)
            {
                maxY = c.getY();
            }
            if(c.getY() < minY)
            {
                minY = c.getY();
            }
        }
        int x = (int) Math.round(PREVIEW_WIDTH * cellSize / 2 - cellSize * (minX + maxX + 1) / 2);
        int y = (int) Math.round(PREVIEW_HEIGHT * cellSize / 2 - cellSize * (minY + maxY + 1) / 2);

        return new Point(x, y);
    };
    
    private void drawRectangle(Canvas canvas, Rectangle rectangle, String color)
    {
        canvas.getContext2d().rect(rectangle.left, rectangle.top, rectangle.getWidth(), rectangle.getHeight());
        canvas.getContext2d().setFillStyle(color);
        canvas.getContext2d().fill();
    }

    private void drawCellIntoRectangle(Canvas canvas, Cell cell, Rectangle rectangle)
    {
        Image cellImage = new Image(COLOR_TO_IMAGE.get(cell.getColor().name()));

        canvas.getContext2d().drawImage(ImageElement.as(cellImage.getElement()),
                rectangle.left, rectangle.top,
                rectangle.getWidth(), rectangle.getHeight());
    }

    private void drawCell(Cell c)
    {
        drawCellIntoRectangle(gameCanvas, c, getCellRectangle(c, new Point(0, 0)));
    }

    private void drawPreviewCell(Cell c)
    {
        drawCellIntoRectangle(previewCanvas, c, getCellRectangle(c, getPreviewCellOffset()));
    }

    private void drawGrid()
    {
        for(int i = 0; i < tetrisEngine.getWidth(); i++)
        {
            gameCanvas.getContext2d().beginPath();
            gameCanvas.getContext2d().setLineWidth(1);
            gameCanvas.getContext2d().setStrokeStyle(GRID_COLOR);
            gameCanvas.getContext2d().moveTo(i * cellSize, 0);
            gameCanvas.getContext2d().lineTo(i * cellSize, getGlassHeight());
            gameCanvas.getContext2d().stroke();
        }

        for(int i = 0; i < tetrisEngine.getHeight(); i++)
        {
            gameCanvas.getContext2d().beginPath();
            gameCanvas.getContext2d().setLineWidth(1);
            gameCanvas.getContext2d().setStrokeStyle(GRID_COLOR);
            gameCanvas.getContext2d().moveTo(0, cellSize * i);
            gameCanvas.getContext2d().lineTo(getGlassWidth(), cellSize * i);
            gameCanvas.getContext2d().stroke();
        }
    }

    private void drawStats()
    {
        // TODO
    }

    private void drawSea()
    {
        if(tetrisEngine.getSea() != null)
        {
            for(Cell c : tetrisEngine.getSea())
            {
                drawCell(c);
            }
        }
    };

    private void drawPiece()
    {
        if(tetrisEngine.getPiece() != null)
            for(Cell c : tetrisEngine.getPiece().getCells())
            {
                drawCell(c);
            }
    };

    private void drawPreview()
    {
        if(tetrisEngine.getNextPiece() != null)
        {
            for(Cell c : tetrisEngine.getNextPiece().getCells())
            {
                drawPreviewCell(c);
            }
        }
    }

    private void initGameCanvasSize()
    {
        gameCanvas.getCanvasElement().setHeight(getGlassHeight());
        gameCanvas.getCanvasElement().setWidth(getGlassWidth());
        cleanCanvas(gameCanvas);
    }

    private void initPreviewCanvasSize()
    {
        previewCanvas.getCanvasElement().setHeight(getPreviewHeight());
        previewCanvas.getCanvasElement().setWidth(getPreviewWidth());
        cleanCanvas(previewCanvas);
    }

    private void cleanCanvas(Canvas canvas)
    {
        canvas.getContext2d().setFillStyle(FIELD_COLOR);
        canvas.getContext2d().rect(0, 0, canvas.getCanvasElement().getWidth(), canvas.getCanvasElement().getHeight());
        canvas.getContext2d().fill();
    }

    private void initInputHandling()
    {
        RootPanel.get().addDomHandler(new KeyPressHandler()
        {
            @Override
            public void onKeyPress(KeyPressEvent event)
            {
                if(event.getCharCode() == 32)
                {
                    tetrisEngine.dropPiece();
                }
            }
        }, KeyPressEvent.getType());
        
        RootPanel.get().addDomHandler(new KeyDownHandler()
        {
            @Override
            public void onKeyDown(KeyDownEvent event)
            {
                if (event.getNativeKeyCode() == 37)
                {
                    tetrisEngine.movePieceLeft();
                }

                if (event.getNativeKeyCode() == 39)
                {
                    tetrisEngine.movePieceRight();
                }

                if (event.getNativeKeyCode() == 40)
                {
                    tetrisEngine.rotatePieceClockwise();
                }

                if (event.getNativeKeyCode() == 38)
                {
                    tetrisEngine.rotatePieceCounterclockwise();
                }
            }
        }, KeyDownEvent.getType());
    };

    private void initCellSize()
    {
        cellSize = getCellSize();
    };

    @Override
    public void refresh()
    {
        cleanCanvas(previewCanvas);
        
        cleanCanvas(gameCanvas);

        if(isGridShown)
        {
            drawGrid();
        }

        drawSea();

        drawPiece();

        if(isPreviewShown)
        {
            drawPreview();
        }
        
        drawStats();
    };
}