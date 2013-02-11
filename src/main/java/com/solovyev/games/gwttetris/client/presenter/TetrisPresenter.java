package com.solovyev.games.gwttetris.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasWidgets;
import com.solovyev.games.gwttetris.client.view.TetrisView;
import com.solovyev.games.tetris.AbstractTetrisEngine;
import com.solovyev.games.tetris.TetrisEngine;
import com.solovyev.games.tetris.TetrisEvent;
import com.solovyev.games.tetris.TetrisListener;

public class TetrisPresenter implements TetrisView.Presenter
{
    private TetrisView tetrisView;    
    private HandlerManager eventBus;
    private TetrisEngine tetrisEngine;

    public TetrisPresenter(TetrisView tetrisView, HandlerManager eventBus)
    {
        this.tetrisView = tetrisView;
        this.eventBus = eventBus;

        tetrisEngine = new AbstractTetrisEngine(10, 20)
        {
            private Timer timer;

            @Override
            protected void startTimer()
            {
                timer = new Timer() {
                    @Override
                    public void run()
                    {
                        timerEvent();
                    }
                };

                timer.scheduleRepeating(getTimerTick());
            }

            @Override
            protected void stopTimer()
            {
                if(timer != null)
                {
                    timer.cancel();
                    timer = null;
                }
            }
        };

        tetrisEngine.addTetrisListener(new TetrisListener()
        {
            @Override
            public void stateChanged(TetrisEvent tetrisEvent)
            {
                TetrisPresenter.this.tetrisView.refresh();
            }
        });

        tetrisView.setPresenter(this);
        
        tetrisEngine.start();

        registerEventHandlers();
    }
    
    @Override
    public void display(HasWidgets container)
    {
        container.clear();
        container.add(tetrisView.asWidget());
    }

    @Override
    public TetrisEngine getTetrisEngine()
    {
        return tetrisEngine;
    }
    
    private void registerEventHandlers()
    {
    }
}
