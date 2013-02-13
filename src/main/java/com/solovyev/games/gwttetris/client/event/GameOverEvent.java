package com.solovyev.games.gwttetris.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.solovyev.games.tetris.TetrisEngine;

public class GameOverEvent extends GwtEvent<GameOverEventHandler>
{
    public static Type<GameOverEventHandler> TYPE = new Type<GameOverEventHandler>();
    private TetrisEngine tetrisEngine;
    
    public GameOverEvent(TetrisEngine tetrisEngine)
    {
        this.tetrisEngine = tetrisEngine;        
    }
    
    public TetrisEngine getTetrisEngine()
    {
        return tetrisEngine;
    }
    @Override
    public Type<GameOverEventHandler> getAssociatedType()
    {
        return TYPE;
    }
    
    @Override
    protected void dispatch(GameOverEventHandler handler)
    {
        handler.handle(this);
    }
}
