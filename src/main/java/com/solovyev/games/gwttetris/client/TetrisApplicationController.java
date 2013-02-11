package com.solovyev.games.gwttetris.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.solovyev.games.gwttetris.client.event.RestartEvent;
import com.solovyev.games.gwttetris.client.event.RestartEventHandler;
import com.solovyev.games.gwttetris.client.presenter.HelloPresenter;
import com.solovyev.games.gwttetris.client.presenter.TetrisPresenter;
import com.solovyev.games.gwttetris.client.view.HelloView;
import com.solovyev.games.gwttetris.client.view.HelloViewImpl;
import com.solovyev.games.gwttetris.client.view.TetrisView;
import com.solovyev.games.gwttetris.client.view.TetrisViewImpl;

public class TetrisApplicationController implements TetrisApplication.Presenter, ValueChangeHandler<String>
{
    private TetrisApplication tetrisApplication;
    private HandlerManager eventBus;
    private HasWidgets container;
    private static final String HELLO_TOKEN = "hello";
    private static final String TETRIS_TOKEN = "tetris";

    public TetrisApplicationController(TetrisApplication tetrisApplication, HandlerManager eventBus)
    {
        this.tetrisApplication = tetrisApplication;
        this.eventBus = eventBus;
        
        tetrisApplication.setPresenter(this);
        
        registerEventHandlers();
    }
    
    @Override
    public void display(HasWidgets container)
    {
        this.container = container;
        
        if ("".equals(History.getToken()))
        {
            // History.newItem(HELLO_TOKEN);
            History.newItem(TETRIS_TOKEN);            
        }
        else
        {
            History.fireCurrentHistoryState();
        }
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event)
    {
        String token = event.getValue();

        System.out.println("===> Browser history event, token: " + event.getValue());

        if (token != null)
        {
            if (token.equals(HELLO_TOKEN))
            {
                makeHelloView();
            }
            else if(token.equals(TETRIS_TOKEN))
            {
                makeTetrisView();
            }
        }
    }

    private void registerEventHandlers()
    {
        History.addValueChangeHandler(this);

        eventBus.addHandler(RestartEvent.TYPE, new RestartEventHandler()
        {
            @Override
            public void restart(RestartEvent restartEvent)
            {
                System.out.println("===> RestartEvent");

                makeHelloView();                
            }
        });
    }
    
    private void makeHelloView()
    {
        HelloView helloView = new HelloViewImpl();
        HelloPresenter helloPresenter = new HelloPresenter(helloView, eventBus);
        helloPresenter.display(container);
    }

    private void makeTetrisView()
    {
        TetrisView tetrisView = new TetrisViewImpl();
        TetrisPresenter tetrisPresenter = new TetrisPresenter(tetrisView, eventBus);
        tetrisPresenter.display(container);
    }
}
