package com.solovyev.games.gwttetris.client.view;

import com.google.gwt.user.client.ui.HasWidgets;


public interface GameOverView
{
    public void setPresenter(Presenter presenter);

    public void showGameOverDialog();

    public void hideGameOverDialog();

    public void showNameInputDialog();

    public void hideNameInputDialog();

    public interface Presenter
    {
        public void handleGameOverOkButton();

        public void handleNameInputOkButton(String name);

        public void handleNameInputCancelButton();

        public void display(HasWidgets container);
    }
}
