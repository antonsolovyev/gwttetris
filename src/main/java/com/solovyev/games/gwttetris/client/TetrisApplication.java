package com.solovyev.games.gwttetris.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

public class TetrisApplication implements EntryPoint
{
    private Presenter presenter;
    
    public void onModuleLoad()
    {
        HandlerManager eventBus = new HandlerManager(null);
        
        TetrisApplicationController tetrisApplicationController = new TetrisApplicationController(this, eventBus);
        
        tetrisApplicationController.display(RootPanel.get());
    }

    public void setPresenter(Presenter presenter)
    {
        this.presenter = presenter;
    }
    
    public interface Presenter
    {
        public void display(HasWidgets container);
    }    
}
