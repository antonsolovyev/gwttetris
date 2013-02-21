package com.solovyev.games.gwttetris.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.solovyev.games.gwttetris.shared.HighScore;


@RemoteServiceRelativePath("highScoreService")
public interface HighScoreService extends RemoteService
{
    public List<HighScore> getHighScores();

    public boolean isHighScore(Integer score);

    public void saveHighScore(HighScore highScore);
}
