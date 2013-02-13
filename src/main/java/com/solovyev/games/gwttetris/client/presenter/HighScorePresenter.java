package com.solovyev.games.gwttetris.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.solovyev.games.gwttetris.client.service.HighScoreServiceAsync;
import com.solovyev.games.gwttetris.client.view.HighScoreView;
import com.solovyev.games.gwttetris.shared.HighScore;

import java.util.Collections;
import java.util.List;

public class HighScorePresenter implements HighScoreView.Presenter
{
    private HighScoreView highScoreView;
    private HandlerManager handlerManager;
    private HighScoreServiceAsync highScoreService;
    
    public HighScorePresenter(HighScoreView highScoreView, HandlerManager eventBus,
        HighScoreServiceAsync highScoreService)
    {
        this.highScoreView = highScoreView;
        this.handlerManager = handlerManager;
        this.highScoreService = highScoreService;
        
        highScoreView.setPresenter(this);

        highScoreService.getHighScores(new AsyncCallback<List<HighScore>>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                throw new RuntimeException("Unable to get high scores", caught);
            }

            @Override
            public void onSuccess(List<HighScore> result)
            {
                HighScorePresenter.this.highScoreView.setHighScores(result);
            }
        });
    }
    
    @Override
    public void display(HasWidgets container)
    {
        highScoreView.show();
    }
}
