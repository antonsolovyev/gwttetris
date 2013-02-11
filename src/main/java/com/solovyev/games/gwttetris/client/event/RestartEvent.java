package com.solovyev.games.gwttetris.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class RestartEvent extends GwtEvent<RestartEventHandler>
{
    public static Type<RestartEventHandler> TYPE = new Type<RestartEventHandler>();

    @Override
    public Type<RestartEventHandler> getAssociatedType()
    {
        return TYPE;
    }
    
    @Override
    protected void dispatch(RestartEventHandler handler)
    {
        handler.restart(this);
    }
}
