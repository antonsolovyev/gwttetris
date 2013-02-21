package com.solovyev.games.gwttetris.client.view;

import java.util.List;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.solovyev.games.gwttetris.shared.HighScore;


public interface HighScoreView extends IsWidget
{
    public void setPresenter(Presenter presenter);

    public void show();

    public void setHighScores(List<HighScore> highScores);

    public interface Presenter
    {
        public void display(HasWidgets container);
    }
}
