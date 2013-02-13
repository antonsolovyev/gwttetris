package com.solovyev.games.gwttetris.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.solovyev.games.gwttetris.client.service.HighScoreService;
import com.solovyev.games.gwttetris.shared.HighScore;

import java.util.List;
import java.util.logging.Logger;

public class HighScoreServiceImpl extends RemoteServiceServlet implements HighScoreService
{
    private static final Logger logger = Logger.getLogger(HighScoreServiceImpl.class.getName());
    
    @Override
    public List<HighScore> getHighScores()
    {
        logger.info("===> getHighScores()");
        
        return null;
    }

    @Override
    public boolean isHighScore(Integer value)
    {
        logger.info("===> isHighScore(), value: " + value);
        
        return true;
    }

    @Override
    public boolean saveHighScore(HighScore highScore)
    {
        logger.info("===> saveHighScore(), highScore: " + highScore);

        return true;
    }
}
