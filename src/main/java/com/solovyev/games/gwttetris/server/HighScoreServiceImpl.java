package com.solovyev.games.gwttetris.server;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.solovyev.games.gwttetris.client.service.HighScoreService;
import com.solovyev.games.gwttetris.server.dao.HighScoreDao;
import com.solovyev.games.gwttetris.shared.HighScore;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


public class HighScoreServiceImpl extends RemoteServiceServlet implements HighScoreService, Controller,
    ServletContextAware
{
    private static final Logger logger = Logger.getLogger(HighScoreServiceImpl.class.getName());

    private ServletContext servletContext;

    private HighScoreDao highScoreDao;

    public HighScoreServiceImpl(HighScoreDao highScoreDao)
    {
        this.highScoreDao = highScoreDao;
    }

    @Override
    public List<HighScore> getHighScores()
    {
        return highScoreDao.getHighScores();
    }

    @Override
    public boolean isHighScore(Integer score)
    {
        return highScoreDao.isHighScore(score);
    }

    @Override
    public void saveHighScore(HighScore highScore)
    {
        highScoreDao.saveHighScore(highScore);
    }

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        super.doPost(request, response);

        return null;
    }

    @Override
    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }

    @Override
    public ServletContext getServletContext()
    {
        return servletContext;
    }
}
