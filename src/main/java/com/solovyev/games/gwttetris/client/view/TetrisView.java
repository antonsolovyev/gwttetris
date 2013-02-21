package com.solovyev.games.gwttetris.client.view;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.solovyev.games.tetris.TetrisEngine;


// There's not a good MVP going on here. Presenter should be smarter and View should be
// dumber. In a sense TetrisEngine is a presenter. There probably is no need for MVP
// here at all, but I am leaving it as an exercise.
public interface TetrisView extends IsWidget
{
    public void refresh();

    public void setPresenter(Presenter presenter);

    public interface Presenter
    {
        public void handleHighScoreButton();

        public TetrisEngine getTetrisEngine();

        public void display(HasWidgets container);
    }
}
