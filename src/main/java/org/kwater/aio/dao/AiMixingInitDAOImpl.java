package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiProcessInitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AiMixingInitDAOImpl implements IAiProcessInitDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiProcessInitDTO> select()
    {
        String strQuery = "SELECT * FROM ai_mixing_init;";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AiProcessInitDTO.class));
    }

    @Override
    public AiProcessInitDTO select(String item)
    {
        String strQuery = "SELECT * FROM ai_mixing_init WHERE item=?";
        try
        {
            return jdbcTemplate.queryForObject(
                    strQuery, new Object[]{item}, new BeanPropertyRowMapper<>(AiProcessInitDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int updateOperationMode(int operation_mode)
    {
        String strQuery = "UPDATE ai_mixing_init SET value=? WHERE item='d_operation_mode'";
        return jdbcTemplate.update(strQuery, operation_mode);
    }

    @Override
    public int update(String item, float value)
    {
        String strQuery = "UPDATE ai_mixing_init SET value=? WHERE item=?";
        return jdbcTemplate.update(strQuery, value, item);
    }
}
