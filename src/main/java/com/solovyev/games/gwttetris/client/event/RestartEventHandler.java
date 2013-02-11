package com.solovyev.games.gwttetris.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface RestartEventHandler extends EventHandler
{
    public void restart(RestartEvent restartEvent);
}
