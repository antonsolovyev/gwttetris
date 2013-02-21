package com.solovyev.games.gwttetris.client.view;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.solovyev.games.tetris.*;


public class TetrisViewImpl implements TetrisView
{
    private static final int PREVIEW_WIDTH = 5;
    private static final int PREVIEW_HEIGHT = 5;
    private static final double SCREEN_SIZE_RATIO = 0.75;
    private static final String GRID_COLOR = "#404040";
    private static final String FIELD_COLOR = "black";
    private static final String BACKGROUND_COLOR = "gray";
    private static final Map<String, String> COLOR_TO_IMAGE = new HashMap<String, String>()
        {

            {
                put(Cell.Color.CYAN.name(), "cell_cyan.png");
                put(Cell.Color.BLUE.name(), "cell_blue.png");
                put(Cell.Color.ORANGE.name(), "cell_orange.png");
                put(Cell.Color.YELLOW.name(), "cell_yellow.png");
                put(Cell.Color.GREEN.name(), "cell_green.png");
                put(Cell.Color.PURPLE.name(), "cell_magenta.png");
                put(Cell.Color.RED.name(), "cell_red.png");
            }
        };

    private Panel mainPanel;
    private Canvas gameCanvas;
    private Canvas previewCanvas;
    private Label scoreLabel;
    private Label speedLabel;
    private Label linesLabel;
    private Label piecesLabel;
    private Button pauseButton;
    private Button startButton;
    private Button highScoreButton;
    private CheckBox previewCheckBox;
    private CheckBox gridCheckBox;

    private int cellSize;
    private boolean isGridShown = false;
    private boolean isPreviewShown = true;

    private Presenter presenter;
    private TetrisEngine tetrisEngine;

    public TetrisViewImpl()
    {
        if (!Canvas.isSupported())
        {
            throw new RuntimeException("Sorry, browser does not support HTML5 canvas, can't continue.");
        }

        FlexTable flexTable = makeFlexTable();

        mainPanel = makeMainPanel(flexTable);

        initInputHandling2();

    }

    private Panel makeMainPanel(Widget widget)
    {
        VerticalPanel res = new VerticalPanel();
        RootPanel.get().getElement().getStyle().setMargin(0, Style.Unit.PX);
        RootPanel.get().getElement().getStyle().setPadding(0, Style.Unit.PX);
        res.setWidth("100%");
        res.add(widget);
        res.setCellHorizontalAlignment(widget, HasHorizontalAlignment.ALIGN_CENTER);
        res.setCellVerticalAlignment(widget, HasVerticalAlignment.ALIGN_MIDDLE);
        res.getElement().getStyle().setHeight(Window.getClientHeight(), Style.Unit.PX);

        return res;
    }

    private FlexTable makeFlexTable()
    {
        FlexTable res = new FlexTable();

        previewCanvas = Canvas.createIfSupported();
        res.setWidget(0, 0, previewCanvas);

        gameCanvas = Canvas.createIfSupported();
        res.setWidget(0, 1, gameCanvas);

        Panel scorePanel = makeStatsPanel();
        res.setWidget(1, 0, scorePanel);

        Panel controlPanel = makeControlPanel();
        res.setWidget(0, 2, controlPanel);

        res.getFlexCellFormatter().setRowSpan(0, 1, 2);
        res.getFlexCellFormatter().setRowSpan(0, 2, 2);
        res.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        res.getFlexCellFormatter().setHeight(0, 0, String.valueOf(previewCanvas.getCanvasElement().getHeight()));
        res.getFlexCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
        res.getFlexCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        res.getElement().getStyle().setBackgroundColor(BACKGROUND_COLOR);

        return res;
    }

    private Panel makeStatsPanel()
    {
        VerticalPanel res = new VerticalPanel();
        res.setWidth("100%");
//        panel.getElement().getStyle().setBackgroundColor("blue");
//        panel.getElement().getStyle().setBorderColor("red");
//        panel.setBorderWidth(10);

        scoreLabel = new Label();
        res.add(makeStatsEntryPanel("Score:", scoreLabel));
        speedLabel = new Label();
        res.add(makeStatsEntryPanel("Speed:", speedLabel));
        linesLabel = new Label();
        res.add(makeStatsEntryPanel("Lines:", linesLabel));
        piecesLabel = new Label();
        res.add(makeStatsEntryPanel("Pieces:", piecesLabel));

        return res;
    }

    private Panel makeStatsEntryPanel(String name, Label valueLabel)
    {
        DockPanel res = new DockPanel();
        res.setWidth("100%");
        res.add(new Label(name), DockPanel.WEST);
        valueLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        res.add(valueLabel, DockPanel.EAST);

        return res;
    }

    private Panel makeControlPanel()
    {
        VerticalPanel res = new VerticalPanel();
//        controlPanel.getElement().getStyle().setBackgroundColor("blue");
//        controlPanel.getElement().getStyle().setBorderColor("red");
//        controlPanel.setBorderWidth(10);

        startButton = new Button("New game");
        startButton.setWidth("100%");
        startButton.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                    tetrisEngine.stop();
                    tetrisEngine.start();

                    pauseButton.setText("Pause");

                    forceDefaultFocus(startButton);
                }
            });
        res.add(startButton);

        pauseButton = new Button("Pause");
        pauseButton.setWidth("100%");
        pauseButton.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                    if (tetrisEngine.getGameState() == TetrisEngine.GameState.PAUSED)
                    {
                        pauseButton.setText("Pause");
                        tetrisEngine.resume();
                    }
                    else if (tetrisEngine.getGameState() == TetrisEngine.GameState.RUNNING)
                    {
                        pauseButton.setText("Resume");
                        tetrisEngine.pause();
                    }

                    forceDefaultFocus(pauseButton);
                }
            });
        res.add(pauseButton);

        highScoreButton = new Button("High Scores");
        highScoreButton.setWidth("100%");
        highScoreButton.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                    presenter.handleHighScoreButton();

                    forceDefaultFocus(highScoreButton);
                }
            });
        res.add(highScoreButton);

        previewCheckBox = new CheckBox("Show preview");
        previewCheckBox.setValue(true);
        previewCheckBox.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                    if (previewCheckBox.getValue())
                    {
                        isPreviewShown = true;
                    }
                    else
                    {
                        isPreviewShown = false;
                    }
                    refresh();

                    forceDefaultFocus(previewCheckBox);
                }
            });
        res.add(previewCheckBox);

        gridCheckBox = new CheckBox("Show grid");
        gridCheckBox.setValue(false);
        gridCheckBox.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                    if (gridCheckBox.getValue())
                    {
                        isGridShown = true;
                    }
                    else
                    {
                        isGridShown = false;
                    }
                    refresh();

                    forceDefaultFocus(gridCheckBox);
                }
            });
        res.add(gridCheckBox);

        return res;
    }

    private void forceDefaultFocus(Focusable focusable)
    {
        focusable.setFocus(false);
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
        return mainPanel;
    }

    private int getCellSize()
    {
        double res = Math.min(
                Window.getClientHeight() * SCREEN_SIZE_RATIO / tetrisEngine.getHeight(),
                Window.getClientWidth() * SCREEN_SIZE_RATIO / (tetrisEngine.getWidth() + PREVIEW_WIDTH));

        return (int) Math.round(res);
    }

    private Rectangle getCellRectangle(Cell cell, Point offset)
    {
        int left = offset.x + (cell.getX() * cellSize);
        int top = offset.y + (cell.getY() * cellSize);
        int right = left + cellSize;
        int bottom = top + cellSize;

        return new Rectangle(left, top, right, bottom);
    }

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
        for (Cell c : tetrisEngine.getNextPiece().getCells())
        {
            if (c.getX() > maxX)
            {
                maxX = c.getX();
            }
            if (c.getX() < minX)
            {
                minX = c.getX();
            }
            if (c.getY() > maxY)
            {
                maxY = c.getY();
            }
            if (c.getY() < minY)
            {
                minY = c.getY();
            }
        }

        int x = (int) Math.round((PREVIEW_WIDTH * cellSize / 2) - (cellSize * (minX + maxX + 1) / 2));
        int y = (int) Math.round((PREVIEW_HEIGHT * cellSize / 2) - (cellSize * (minY + maxY + 1) / 2));

        return new Point(x, y);
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
        for (int i = 0; i < tetrisEngine.getWidth(); i++)
        {
            gameCanvas.getContext2d().beginPath();
            gameCanvas.getContext2d().setLineWidth(1);
            gameCanvas.getContext2d().setStrokeStyle(GRID_COLOR);
            gameCanvas.getContext2d().moveTo(i * cellSize, 0);
            gameCanvas.getContext2d().lineTo(i * cellSize, getGlassHeight());
            gameCanvas.getContext2d().stroke();
        }

        for (int i = 0; i < tetrisEngine.getHeight(); i++)
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
        scoreLabel.setText(String.valueOf(tetrisEngine.getScore()));
        speedLabel.setText(String.valueOf(tetrisEngine.getSpeed()));
        piecesLabel.setText(String.valueOf(tetrisEngine.getPieceCount()));
        linesLabel.setText(String.valueOf(tetrisEngine.getLineCount()));
    }

    private void drawSea()
    {
        if (tetrisEngine.getSea() != null)
        {
            for (Cell c : tetrisEngine.getSea())
            {
                drawCell(c);
            }
        }
    }

    private void drawPiece()
    {
        if (tetrisEngine.getPiece() != null)
        {
            for (Cell c : tetrisEngine.getPiece().getCells())
            {
                drawCell(c);
            }
        }
    }

    private void drawPreview()
    {
        if (tetrisEngine.getNextPiece() != null)
        {
            for (Cell c : tetrisEngine.getNextPiece().getCells())
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
                    if (event.getCharCode() == 32)
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
    }

    private void initInputHandling2()
    {
        Event.addNativePreviewHandler(new Event.NativePreviewHandler()
            {
                public void onPreviewNativeEvent(Event.NativePreviewEvent event)
                {
                    NativeEvent nativeEvent = event.getNativeEvent();
                    int typeInt = event.getTypeInt();

                    switch (typeInt)
                    {
                    case Event.ONKEYDOWN:
                        if (nativeEvent.getKeyCode() == 37)
                        {
                            tetrisEngine.movePieceLeft();
                        }

                        if (nativeEvent.getKeyCode() == 39)
                        {
                            tetrisEngine.movePieceRight();
                        }

                        if (nativeEvent.getKeyCode() == 40)
                        {
                            tetrisEngine.rotatePieceClockwise();
                        }

                        if (nativeEvent.getKeyCode() == 38)
                        {
                            tetrisEngine.rotatePieceCounterclockwise();
                        }
                        break;
                    case Event.ONKEYPRESS:
                        if (nativeEvent.getCharCode() == 32)
                        {
                            tetrisEngine.dropPiece();
                        }
                        break;
                    default:
                        break;
                    }
                }
            });
    }

    private void initCellSize()
    {
        cellSize = getCellSize();
    }

    @Override
    public void refresh()
    {
        cleanCanvas(previewCanvas);

        cleanCanvas(gameCanvas);

        if (isGridShown)
        {
            drawGrid();
        }

        drawSea();

        drawPiece();

        if (isPreviewShown)
        {
            drawPreview();
        }

        drawStats();
    }

    private static class Point
    {
        public int x;
        public int y;

        public Point(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }

    private static class Rectangle
    {
        public int top;
        public int right;
        public int bottom;
        public int left;

        public Rectangle(int left, int top, int right, int bottom)
        {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public int getHeight()
        {
            return bottom - top;
        }

        public int getWidth()
        {
            return right - left;
        }
    }
}
