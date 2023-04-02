package org.kwater.aio.dao;

import org.kwater.aio.dto.LoginHistoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class LoginHistoryDAOImpl implements ILoginHistoryDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<LoginHistoryDTO> select()
    {
        String strQuery = "select * from login_history order by history_index desc limit 100";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(LoginHistoryDTO.class));
    }

    @Override
    public int insert(LoginHistoryDTO dto)
    {
        String strQuery = "insert into login_history (userid, name, type, timestamp, address) values (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getUserid(), dto.getName(), dto.getType(), dto.getTimestamp(), dto.getAddress()
        );
    }

    @Override
    public int delete(Date date)
    {
        String strQuery = "delete from login_history where timestamp < ?";
        return jdbcTemplate.update(strQuery, date);
    }
}
