package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiMixingRealtimeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiMixingRealtimeDAOImpl implements IAiMixingRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiMixingRealtimeDTO> select(Date start_time, Date end_time)
    {
        String strQuery = "SELECT * FROM ai_mixing_realtime " +
                "WHERE update_time > ? AND update_time <= ? ORDER BY update_time";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiMixingRealtimeDTO.class)
        );
    }

    @Override
    public AiMixingRealtimeDTO select()
    {
        String strQuery = "SELECT * FROM ai_mixing_realtime ORDER BY update_time DESC LIMIT 1";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(AiMixingRealtimeDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int delete(Date update_time)
    {
        String strQuery = "DELETE FROM ai_mixing_realtime WHERE update_time < ?";
        return jdbcTemplate.update(strQuery, update_time);
    }
}
