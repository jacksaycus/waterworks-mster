package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiDisinfectionRealtimeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiDisinfectionRealtimeDAOImpl implements IAiDisinfectionRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiDisinfectionRealtimeDTO> select(Date start_time, Date end_time)
    {
        String strQuery = "SELECT * FROM ai_disinfection_realtime " +
                "WHERE update_time > ? AND update_time <= ? ORDER BY update_time";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiDisinfectionRealtimeDTO.class)
        );
    }

    @Override
    public AiDisinfectionRealtimeDTO select()
    {
        String strQuery = "SELECT * FROM ai_disinfection_realtime ORDER BY update_time DESC LIMIT 1";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(AiDisinfectionRealtimeDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int delete(Date update_time)
    {
        String strQuery = "DELETE FROM ai_disinfection_realtime WHERE update_time < ?";
        return jdbcTemplate.update(strQuery, update_time);
    }
}
