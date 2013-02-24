package com.solovyev.games.gwttetris.client.presenter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.solovyev.games.gwttetris.client.service.HighScoreServiceAsync;
import com.solovyev.games.gwttetris.client.view.HighScoreView;
import com.solovyev.games.gwttetris.shared.HighScore;


public class HighScorePresenter implements HighScoreView.Presenter
{
    private static final Logger logger = Logger.getLogger(HighScorePresenter.class.getName());

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
    }

    @Override
    public void display(HasWidgets container)
    {
        populateShowHighScores();
    }

    private void populateShowHighScores()
    {
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
                    logger.log(Level.FINE, "res: " + result);

                    HighScorePresenter.this.highScoreView.setHighScores(result);

                    highScoreView.show();
                }
            });
    }
}
