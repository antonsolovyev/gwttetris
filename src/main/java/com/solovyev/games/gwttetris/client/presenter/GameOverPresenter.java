package com.solovyev.games.gwttetris.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.solovyev.games.gwttetris.client.event.ShowHighScoresEvent;
import com.solovyev.games.gwttetris.client.service.HighScoreServiceAsync;
import com.solovyev.games.gwttetris.client.view.GameOverView;
import com.solovyev.games.gwttetris.shared.HighScore;
import com.solovyev.games.tetris.TetrisEngine;

import java.util.Date;

public class GameOverPresenter implements GameOverView.Presenter
{
    private GameOverView gameOverView;
    private HandlerManager eventBus;
    private TetrisEngine tetrisEngine;
    private HighScoreServiceAsync highScoreService;
           
    public GameOverPresenter(GameOverView gameOverView, HandlerManager eventBus,
        TetrisEngine tetrisEngine, HighScoreServiceAsync highScoreService)
    {
        this.gameOverView = gameOverView;
        this.eventBus = eventBus;
        this.tetrisEngine = tetrisEngine;
        this.highScoreService = highScoreService;
        
        gameOverView.setPresenter(this);
        
        registerHandlers();
    }
    
    @Override
    public void handleGameOverOkButton()
    {
        gameOverView.hideGameOverDialog();
       
        checkHighScore();
    }
    
    @Override
    public void handleNameInputOkButton(String name)
    {
        gameOverView.hideNameInputDialog();
        
        highScoreService.saveHighScore(new HighScore(name, tetrisEngine.getScore(), new Date()), new AsyncCallback<Void>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                throw new RuntimeException("Problem saving high score: ", caught);
            }

            @Override
            public void onSuccess(Void v)
            {
                eventBus.fireEvent(new ShowHighScoresEvent());
            }
        });        
    }
    
    @Override
    public void handleNameInputCancelButton()
    {
        gameOverView.hideNameInputDialog();
    }

    @Override
    public void display(HasWidgets container)
    {
        gameOverView.showGameOverDialog();
    }
    
    private void registerHandlers()
    {
    }
    
    private void checkHighScore()
    {
        highScoreService.isHighScore(tetrisEngine.getScore(), new AsyncCallback<Boolean>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                throw new RuntimeException("Unable to check whether the result is a high score", caught);
            }

            @Override
            public void onSuccess(Boolean result)
            {
                if(result)
                {
                    gameOverView.showNameInputDialog();
                }
            }
        });
    }
}
