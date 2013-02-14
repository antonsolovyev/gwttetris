package com.solovyev.games.gwttetris.client.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface HighScoreServiceAsync
{

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see com.solovyev.games.gwttetris.client.service.HighScoreService
     */
    void getHighScores( AsyncCallback<java.util.List<com.solovyev.games.gwttetris.shared.HighScore>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see com.solovyev.games.gwttetris.client.service.HighScoreService
     */
    void isHighScore( java.lang.Integer value, AsyncCallback<java.lang.Boolean> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see com.solovyev.games.gwttetris.client.service.HighScoreService
     */
    void saveHighScore( com.solovyev.games.gwttetris.shared.HighScore highScore, AsyncCallback<Void> callback );


    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static HighScoreServiceAsync instance;

        public static final HighScoreServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (HighScoreServiceAsync) GWT.create( HighScoreService.class );
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint( GWT.getModuleBaseURL() + "highScoreService" );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }
}
