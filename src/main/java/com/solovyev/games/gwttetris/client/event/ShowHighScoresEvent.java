package com.solovyev.games.gwttetris.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowHighScoresEvent extends GwtEvent<ShowHighScoresEventHandler>
{
    public static Type<ShowHighScoresEventHandler> TYPE = new Type<ShowHighScoresEventHandler>();

    @Override
    public Type<ShowHighScoresEventHandler> getAssociatedType()
    {
        return TYPE;
    }
    
    @Override
    protected void dispatch(ShowHighScoresEventHandler handler)
    {
        handler.showHighScores(this);
    }
}
