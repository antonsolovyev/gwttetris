package com.solovyev.games.gwttetris.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.solovyev.games.gwttetris.client.dialog.GameOverDialog;
import com.solovyev.games.gwttetris.client.dialog.HighScoreDialog;
import com.solovyev.games.gwttetris.client.dialog.TetrisDialog;
import com.solovyev.games.gwttetris.client.event.GameOverEvent;
import com.solovyev.games.gwttetris.client.event.GameOverEventHandler;
import com.solovyev.games.gwttetris.client.event.ShowHighScoresEvent;
import com.solovyev.games.gwttetris.client.event.ShowHighScoresEventHandler;
import com.solovyev.games.gwttetris.client.service.HighScoreService;
import com.solovyev.games.gwttetris.client.service.HighScoreServiceAsync;
import com.solovyev.games.tetris.TetrisEngine;


public class TetrisApplication implements EntryPoint, ValueChangeHandler<String>
{
    private static final Logger logger = Logger.getLogger(TetrisApplication.class.getName());
    private static final String TETRIS_TOKEN = "tetris";
    private HandlerManager eventBus;
    private HasWidgets container;
    private HighScoreServiceAsync highScoreService;

    public void onModuleLoad()
    {
        setUngaughtExceptionHandler();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand()
            {
                @Override
                public void execute()
                {
                    init();
                    run();
                }
            });
    }

    private void init()
    {
        eventBus = new HandlerManager(null);

        container = RootPanel.get();

        highScoreService = GWT.create(HighScoreService.class);

        registerEventHandlers();
    }

    private void setUngaughtExceptionHandler()
    {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler()
            {
                public void onUncaughtException(Throwable e)
                {
                    logger.log(Level.SEVERE, "exception in client code", e);

                    Window.alert("Error: " + e);
                }
            });
    }

    private void run()
    {
        if ("".equals(History.getToken()))
        {
            History.newItem(TETRIS_TOKEN);
        }
        else
        {
            History.fireCurrentHistoryState();
        }
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event)
    {
        String token = event.getValue();

        if (token != null)
        {
            if (token.equals(TETRIS_TOKEN))
            {
                makeTetrisDialog();
            }
        }
    }

    private void registerEventHandlers()
    {
        History.addValueChangeHandler(this);

        eventBus.addHandler(GameOverEvent.TYPE, new GameOverEventHandler()
            {
                @Override
                public void handle(GameOverEvent gameOverEvent)
                {
                    makeGameOverDialog(gameOverEvent.getTetrisEngine());
                }
            });

        eventBus.addHandler(ShowHighScoresEvent.TYPE, new ShowHighScoresEventHandler()
            {
                @Override
                public void showHighScores(ShowHighScoresEvent event)
                {
                    makeHighScoreDialog();
                }
            });

    }

    private void makeTetrisDialog()
    {
        TetrisDialog tetrisDialog = new TetrisDialog(eventBus);
        tetrisDialog.display(container);
    }

    private void makeGameOverDialog(TetrisEngine tetrisEngine)
    {
        GameOverDialog gameOverDialog = new GameOverDialog(eventBus, tetrisEngine, highScoreService);
        gameOverDialog.display(container);
    }

    private void makeHighScoreDialog()
    {
        HighScoreDialog highScoreDialog = new HighScoreDialog(eventBus, highScoreService);
        highScoreDialog.display(container);
    }
}
