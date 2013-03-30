package com.solovyev.games.gwttetris.client.dialog;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.solovyev.games.gwttetris.client.event.GameOverEvent;
import com.solovyev.games.gwttetris.client.event.ShowHighScoresEvent;
import com.solovyev.games.tetris.*;


public class TetrisDialog implements Dialog
{
    private static TetrisDialogUiBinder tetrisDialogUiBinder = GWT.create(TetrisDialogUiBinder.class);

    private static final int GLASS_WIDTH = 10;
    private static final int GLASS_HEIGHT = 20;
    private static final int PREVIEW_WIDTH = 5;
    private static final int PREVIEW_HEIGHT = 5;
    private static final double SCREEN_SIZE_RATIO = 0.75;
    private static final String GRID_COLOR = "#404040";
    private static final String FIELD_COLOR = "black";
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

    @UiField
    public Canvas gameCanvas;

    @UiField
    public Canvas previewCanvas;

    @UiField
    public Label scoreLabel;

    @UiField
    public Label speedLabel;

    @UiField
    public Label linesLabel;

    @UiField
    public Label piecesLabel;

    @UiField
    public Button pauseButton;

    @UiField
    public Button startButton;

    @UiField
    public Button highScoreButton;

    @UiField
    public CheckBox previewCheckBox;

    @UiField
    public CheckBox gridCheckBox;

    private HTMLPanel htmlPanel;
    private int cellSize;
    private TetrisEngine tetrisEngine;
    private final HandlerManager eventBus;

    public TetrisDialog(HandlerManager eventBus)
    {
        this.eventBus = eventBus;

        if (!Canvas.isSupported())
        {
            throw new RuntimeException("Sorry, browser does not support HTML5 canvas, can't continue.");
        }

        initTetrisEngine();

        htmlPanel = tetrisDialogUiBinder.createAndBindUi(this);

        initCellSize();

        initGameCanvasSize();

        initPreviewCanvasSize();

        initInputHandling();

        tetrisEngine.start();
    }

    @Override
    public void display(HasWidgets container)
    {
        container.clear();
        container.add(htmlPanel);
    }

    @UiFactory
    public Canvas makeCanvas()
    {
        return Canvas.createIfSupported();
    }

    @UiHandler("gridCheckBox")
    public void gridCheckBoxHandler(ClickEvent clickEvent)
    {
        refresh();

        forceDefaultFocus(gridCheckBox);
    }

    @UiHandler("previewCheckBox")
    public void previewCheckBoxHandler(ClickEvent clickEvent)
    {
        refresh();

        forceDefaultFocus(previewCheckBox);
    }

    @UiHandler("highScoreButton")
    public void highScoreButtonHandler(ClickEvent clickEvent)
    {
        eventBus.fireEvent(new ShowHighScoresEvent());

        forceDefaultFocus(highScoreButton);
    }

    @UiHandler("pauseButton")
    public void pauseButtonHandler(ClickEvent clickEvent)
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

    @UiHandler("startButton")
    public void startButtonHandler(ClickEvent clickEvent)
    {
        tetrisEngine.stop();
        tetrisEngine.start();

        pauseButton.setText("Pause");

        forceDefaultFocus(startButton);
    }

    private void initTetrisEngine()
    {
        tetrisEngine = new AbstractTetrisEngine(GLASS_WIDTH, GLASS_HEIGHT)
            {
                private Timer timer;

                @Override
                protected void startTimer()
                {
                    timer = new Timer()
                        {
                            @Override
                            public void run()
                            {
                                timerEvent();
                            }
                        };

                    timer.scheduleRepeating(getTimerTick());
                }

                @Override
                protected void stopTimer()
                {
                    if (timer != null)
                    {
                        timer.cancel();
                        timer = null;
                    }
                }
            };

        tetrisEngine.addTetrisListener(new TetrisListener()
            {
                private TetrisEngine.GameState previousGameState;

                @Override
                public void stateChanged(TetrisEvent tetrisEvent)
                {
                    if ((tetrisEngine.getGameState() == TetrisEngine.GameState.GAMEOVER) &&
                            (tetrisEngine.getGameState() != previousGameState))
                    {
                        TetrisDialog.this.eventBus.fireEvent(new GameOverEvent(tetrisEngine));
                    }
                    previousGameState = tetrisEngine.getGameState();

                    TetrisDialog.this.refresh();
                }
            });
    }

    private void forceDefaultFocus(Focusable focusable)
    {
        focusable.setFocus(false);
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

        int x = Math.round((PREVIEW_WIDTH * cellSize / 2) - (cellSize * (minX + maxX + 1) / 2));
        int y = Math.round((PREVIEW_HEIGHT * cellSize / 2) - (cellSize * (minY + maxY + 1) / 2));

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

    private void refresh()
    {
        cleanCanvas(previewCanvas);

        cleanCanvas(gameCanvas);

        if (gridCheckBox.getValue())
        {
            drawGrid();
        }

        drawSea();

        drawPiece();

        if (previewCheckBox.getValue())
        {
            drawPreview();
        }

        drawStats();
    }

    public interface TetrisDialogUiBinder extends UiBinder<HTMLPanel, TetrisDialog>
    {
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
