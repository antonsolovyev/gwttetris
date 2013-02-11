package com.solovyev.games.gwttetris.client.view;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.solovyev.games.tetris.TetrisEngine;

public interface TetrisView extends IsWidget
{
    public void refresh();
    
    public void setPresenter(Presenter presenter);
    
    public interface Presenter
    {
        public TetrisEngine getTetrisEngine();
        
        public void display(HasWidgets container);
    }
}
