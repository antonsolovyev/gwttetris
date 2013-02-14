package com.solovyev.games.gwttetris.server.dao;

import com.solovyev.games.gwttetris.shared.HighScore;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class HighScoreDaoImpl implements HighScoreDao
{
    private static final Logger logger = Logger.getLogger(HighScoreDaoImpl.class.getName());

    private static final int MAX_HIGH_SCORE_RECORDS = 10;

    private SimpleJdbcTemplate simpleJdbcTemplate;

    public HighScoreDaoImpl(JdbcDataSource jdbcDataSource)
    {
        this.simpleJdbcTemplate = new SimpleJdbcTemplate(jdbcDataSource);
    }

    @Override
    public List<HighScore> getHighScores()
    {
        List<HighScore> highScores = simpleJdbcTemplate.query("select * from high_score;", new ParameterizedRowMapper<HighScore>()
        {
            @Override
            public HighScore mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                String name = rs.getString("name");
                int score = rs.getInt("score");
                Date date = rs.getDate("date");

                return new HighScore(name, score, date);
            };
        });

        return highScores;
    }

    @Override
    public boolean isHighScore(Integer value)
    {
        Map<String, Object> map = simpleJdbcTemplate.queryForMap("select count(*) as count, min(score) as min from high_score;");
        Long count = (Long) map.get("COUNT");
        Integer min = (Integer) map.get("MIN");

        boolean res = false;
        if(count < MAX_HIGH_SCORE_RECORDS || (min != null && value > min))
        {
            res = true;
        }
        
        return res;
    }

    @Override
    public void saveHighScore(HighScore highScore)
    {
        simpleJdbcTemplate.update("insert into high_score (name, score, date) values (?, ?, ?)",
                highScore.getName(), highScore.getValue(), highScore.getDate());
    }
}