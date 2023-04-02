package org.kwater.aio.dao;

import org.kwater.aio.dto.AccessTokenDTO;
import org.kwater.aio.dto.IAccessTokenDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AccessTokenDAOImpl implements IAccessTokenDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(AccessTokenDTO dto)
    {
        String strQuery = "insert into access_token (access_token, userid, name, authority, expiration) values (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getAccess_token(), dto.getUserid(), dto.getName(), dto.getAuthority(), dto.getExpiration()
        );
    }

    @Override
    public List<AccessTokenDTO> select() {
        String strQuery = "select * from access_token";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AccessTokenDTO.class));
    }

    @Override
    public AccessTokenDTO select(String token)
    {
        String strQuery = "select * from access_token where access_token=?";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new Object[]{token}, new BeanPropertyRowMapper<>(AccessTokenDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int update(String token, Date expiration)
    {
        String strQuery = "update access_token set expiration=? where access_token=?";
        return jdbcTemplate.update(strQuery, expiration, token);
    }

    @Override
    public int delete(String token)
    {
        String strQuery = "delete from access_token where access_token=?";
        return jdbcTemplate.update(strQuery, token);
    }

    @Override
    public int delete(Date date)
    {
        String strQuery = "delete from access_token where expiration<?";
        return jdbcTemplate.update(strQuery, date);
    }
}
