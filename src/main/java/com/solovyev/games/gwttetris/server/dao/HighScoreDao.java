package com.solovyev.games.gwttetris.server.dao;

import java.util.List;

import com.solovyev.games.gwttetris.shared.HighScore;

import org.springframework.transaction.annotation.Transactional;


public interface HighScoreDao
{
    public static final int MAX_HIGH_SCORE_RECORDS = 10;

    public List<HighScore> getHighScores();

    public boolean isHighScore(Integer score);

    public void saveHighScore(HighScore highScore);
}
