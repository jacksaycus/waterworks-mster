package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiProcessControlDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiPreDisinfectionControlDAOImpl implements IAiProcessControlDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public AiProcessControlDTO select(Date update_time, Date run_time, String name, int kafka_flag)
    {
        String strQuery = "SELECT * FROM ai_pre_disinfection_control WHERE update_time=? and run_time=? and name=? and kafka_flag=?";

        try
        {
            return jdbcTemplate.queryForObject(
                    strQuery,
                    new Object[]{update_time, run_time, name, kafka_flag},
                    new BeanPropertyRowMapper<>(AiProcessControlDTO.class)
            );
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public List<AiProcessControlDTO> select(Date run_time, int kafka_flag)
    {
        String strQuery = "SELECT * FROM " +
                "(SELECT * FROM ai_pre_disinfection_control WHERE run_time>=? and kafka_flag=? ORDER BY run_time DESC LIMIT 100) " +
                "ai_pre_disinfection_control GROUP BY name";
        return jdbcTemplate.query (
                strQuery,
                new Object[]{run_time, kafka_flag},
                new BeanPropertyRowMapper<>(AiProcessControlDTO.class)
        );
    }

    @Override
    public int updateKafkaFlag(Date update_time, Date run_time, String name, int kafka_flag)
    {
        String strQuery = "UPDATE ai_pre_disinfection_control SET kafka_flag=? WHERE update_time=? and run_time=? and name=?";
        return jdbcTemplate.update(
                strQuery,
                kafka_flag, update_time, run_time, name
        );
    }

    @Override
    public int delete(Date date)
    {
        String strQuery = "DELETE FROM ai_pre_disinfection_control WHERE update_time < ?";
        return jdbcTemplate.update(strQuery, date);
    }
}
