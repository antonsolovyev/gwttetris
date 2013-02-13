package com.solovyev.games.gwttetris.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface GameOverEventHandler extends EventHandler
{
    public void handle(GameOverEvent gameOverEvent);
}
