package com.solovyev.games.gwttetris.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;


public class TetrisApplication implements EntryPoint
{
    private static final Logger logger = Logger.getLogger(TetrisApplication.class.getName());

    private Presenter presenter;

    public void onModuleLoad()
    {
        setUngaughtExceptionHandler();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand()
            {
                @Override
                public void execute()
                {
                    init();
                }
            });
    }

    private void init()
    {
        HandlerManager eventBus = new HandlerManager(null);

        TetrisApplicationController tetrisApplicationController = new TetrisApplicationController(this, eventBus);

        tetrisApplicationController.display(RootPanel.get());
    }

    public void setPresenter(Presenter presenter)
    {
        this.presenter = presenter;
    }

    private void setUngaughtExceptionHandler()
    {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler()
            {
                public void onUncaughtException(Throwable e)
                {
                    logger.log(Level.SEVERE, "exception in client code", e);

                    Window.alert("Error: " + e);
                }
            });
    }

    public interface Presenter
    {
        public void display(HasWidgets container);
    }
}
