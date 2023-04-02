package org.kwater.aio.dao;

import org.kwater.aio.dto.ProcessCodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProcessCodeDAOImpl implements IProcessCodeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<ProcessCodeDTO> select()
    {
        String strQuery = "select code_index, code, english, korean, IF(is_use, 'true', 'false') as is_use " +
                "from process_code order by code_index";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(ProcessCodeDTO.class));
    }
}
