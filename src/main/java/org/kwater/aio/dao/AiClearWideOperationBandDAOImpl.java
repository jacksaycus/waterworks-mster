package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiClearOperationBandDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiClearWideOperationBandDAOImpl implements IAiClearOperationBandDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<AiClearOperationBandDTO> select(Date start_time, Date end_time)
    {
        String strQuery = "SELECT * FROM ai_clear_wide_operation_band " +
                "WHERE `index` > ? and `index` <= ? ORDER BY `index`";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiClearOperationBandDTO.class)
        );
    }
}
