package com.solovyev.games.gwttetris.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface HelloView extends IsWidget
{
    public void setPresenter(Presenter presenter);
    
    public void hello(String message);
    
    public interface Presenter
    {
        public void onHelloButtonClicked(ClickEvent event);

        public void onRestartButtonClicked(ClickEvent event);

        public void display(HasWidgets container);
    }
}
