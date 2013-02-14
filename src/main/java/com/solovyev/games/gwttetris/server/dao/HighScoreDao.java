package com.solovyev.games.gwttetris.server.dao;

import com.solovyev.games.gwttetris.shared.HighScore;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HighScoreDao
{
    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    public List<HighScore> getHighScores();

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    public boolean isHighScore(Integer value);

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    public void saveHighScore(HighScore highScore);
}
