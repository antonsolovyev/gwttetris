package com.solovyev.games.gwttetris.server.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.solovyev.games.gwttetris.shared.HighScore;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;


public class HighScoreDaoImpl implements HighScoreDao
{
    private static final Logger logger = Logger.getLogger(HighScoreDaoImpl.class.getName());

    private static final int MAX_HIGH_SCORE_RECORDS = 10;

    private SimpleJdbcTemplate simpleJdbcTemplate;

    public HighScoreDaoImpl(DataSource dataSource)
    {
        this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

    @Override
    public List<HighScore> getHighScores()
    {
        List<HighScore> highScores = simpleJdbcTemplate.query(
                "select * from high_score order by score desc, date desc;", new ParameterizedRowMapper<HighScore>()
                {
                    @Override
                    public HighScore mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        String name = rs.getString("NAME");
                        Integer score = rs.getInt("SCORE");
                        Date date = rs.getDate("DATE");

                        return new HighScore(name, score, date);
                    }
                    ;
                });

        // This trips silly GWT serialization.
        // List<HighScore> res = highScores.subList(0, MAX_HIGH_SCORE_RECORDS);
        // Workaround
        List<HighScore> res = new ArrayList<HighScore>();
        for (HighScore h : highScores)
        {
            res.add(h);
        }

        return res;
    }

    @Override
    public boolean isHighScore(Integer score)
    {
        Map<String, Object> map = simpleJdbcTemplate.queryForMap(
                "select count(*) as count, min(score) as min from high_score;");
        Long count = (Long) map.get("COUNT");
        Integer min = (Integer) map.get("MIN");

        boolean res = false;
        if ((count < MAX_HIGH_SCORE_RECORDS) || ((min != null) && (score > min)))
        {
            res = true;
        }

        return res;
    }

    @Override
    public void saveHighScore(HighScore highScore)
    {
        simpleJdbcTemplate.update("insert into high_score (name, score, date) values (?, ?, ?)",
            highScore.getName(), highScore.getScore(), highScore.getDate());

        simpleJdbcTemplate.update(
            "delete from high_score where id not in (select top ? id from high_score order by score desc, date desc);",
            MAX_HIGH_SCORE_RECORDS);
    }
}
