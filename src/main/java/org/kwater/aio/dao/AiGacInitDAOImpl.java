package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiProcessInitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AiGacInitDAOImpl implements IAiProcessInitDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiProcessInitDTO> select()
    {
        String strQuery = "SELECT * FROM ai_gac_init";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AiProcessInitDTO.class));
    }

    @Override
    public AiProcessInitDTO select(String item)
    {
        String strQuery = "SELECT * FROM ai_gac_init WHERE item=?";
        try
        {
            return jdbcTemplate.queryForObject(
                    strQuery, new Object[]{item}, new BeanPropertyRowMapper<>(AiProcessInitDTO.class)
            );
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int updateOperationMode(int operation_mode)
    {
        String strQuery = "UPDATE ai_gac_init SET value=? WHERE item='i_operation_mode'";
        return jdbcTemplate.update(strQuery, operation_mode);
    }

    @Override
    public int update(String item, float value)
    {
        String strQuery = "UPDATE ai_gac_init SET value=? WHERE item=?";
        return jdbcTemplate.update(strQuery, value, item);
    }

    public int updateTi(float value)
    {
        String strQuery = "UPDATE ai_gac_init SET VALUE = ? WHERE item REGEXP 'AII-20.'";
        return jdbcTemplate.update(strQuery, value);
    }
}
