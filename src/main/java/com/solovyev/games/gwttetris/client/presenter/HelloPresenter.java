package com.solovyev.games.gwttetris.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.solovyev.games.gwttetris.client.event.RestartEvent;
import com.solovyev.games.gwttetris.client.view.HelloView;
import com.solovyev.games.tetris.*;

import java.util.List;

public class HelloPresenter implements HelloView.Presenter
{
    private HelloView helloView;
    private HandlerManager eventBus;
    private TetrisEngine tetrisEngine;

    @UiField
    public Button helloButton;

    public HelloPresenter(HelloView helloView, HandlerManager eventBus )
    {
        this.helloView = helloView;
        this.eventBus = eventBus;
        
        helloView.setPresenter(this);
        
        registerEventHandlers();        
    }
    
    @Override
    public void display(HasWidgets container)
    {
        container.clear();
        container.add(helloView.asWidget());
    }
    
    @Override
    public void onHelloButtonClicked(ClickEvent event)
    {
        helloView.hello("Hello, world! Here's a random int: " + Random.nextInt());
    }

    @Override
    public void onRestartButtonClicked(ClickEvent event)
    {
        eventBus.fireEvent(new RestartEvent());
    }

    private void registerEventHandlers()
    {        
    }
}
