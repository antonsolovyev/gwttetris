package com.solovyev.games.gwttetris.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.solovyev.games.gwttetris.client.event.*;
import com.solovyev.games.gwttetris.client.presenter.GameOverPresenter;
import com.solovyev.games.gwttetris.client.presenter.HighScorePresenter;
import com.solovyev.games.gwttetris.client.presenter.TetrisPresenter;
import com.solovyev.games.gwttetris.client.service.HighScoreService;
import com.solovyev.games.gwttetris.client.service.HighScoreServiceAsync;
import com.solovyev.games.gwttetris.client.view.*;
import com.solovyev.games.tetris.TetrisEngine;

public class TetrisApplicationController implements TetrisApplication.Presenter, ValueChangeHandler<String>
{
    private static final String TETRIS_TOKEN = "tetris";
    private TetrisApplication tetrisApplication;
    private HandlerManager eventBus;
    private HasWidgets container;

    private HighScoreServiceAsync highScoreService;
    
    public TetrisApplicationController(TetrisApplication tetrisApplication, HandlerManager eventBus)
    {
        this.tetrisApplication = tetrisApplication;
        this.eventBus = eventBus;
        
        highScoreService = GWT.create(HighScoreService.class);    
        
        tetrisApplication.setPresenter(this);
        
        registerEventHandlers();
    }
    
    @Override
    public void display(HasWidgets container)
    {
        this.container = container;
        
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
            if(token.equals(TETRIS_TOKEN))
            {
                makeTetrisView();
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
                makeGameOverView(gameOverEvent.getTetrisEngine());
            }
        });

        eventBus.addHandler(ShowHighScoresEvent.TYPE, new ShowHighScoresEventHandler()
        {
            @Override
            public void showHighScores(ShowHighScoresEvent event)
            {
                makeHighScoreView();
            }
        });

    }
    
    private void makeTetrisView()
    {
        TetrisView tetrisView = new TetrisViewImpl();
        TetrisPresenter tetrisPresenter = new TetrisPresenter(tetrisView, eventBus);
        tetrisPresenter.display(container);
    }
    
    private void makeGameOverView(TetrisEngine tetrisEngine)
    {
        GameOverView gameOverView = new GameOverViewImpl();
        GameOverView.Presenter gameOverPresenter = new GameOverPresenter(gameOverView, eventBus,
                tetrisEngine, highScoreService);
        gameOverPresenter.display(container);
    }
    
    private void makeHighScoreView()
    {
        HighScoreView highScoreView = new HighScoreViewImpl();
        HighScoreView.Presenter highScorePresenter = new HighScorePresenter(highScoreView, eventBus,
            highScoreService);
        highScorePresenter.display(container);
    }
}
