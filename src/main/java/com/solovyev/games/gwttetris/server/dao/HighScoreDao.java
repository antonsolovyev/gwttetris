package com.solovyev.games.gwttetris.server.dao;

import com.solovyev.games.gwttetris.shared.HighScore;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HighScoreDao
{
    @Transactional(readOnly = true)
    public List<HighScore> getHighScores();

    @Transactional(readOnly = true)
    public boolean isHighScore(Integer score);

    @Transactional(readOnly = false)
    public void saveHighScore(HighScore highScore);
}
