package org.kwater.aio.dao;

import org.kwater.aio.dto.UserDTO;
import org.kwater.aio.util.CommonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDAOImpl implements IUserDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(UserDTO dto)
    {
        String strQuery = "insert into user values (?, password(?), ?, ?, ?)";

        try
        {
            return jdbcTemplate.update(
                    strQuery,
                    dto.getUserid(), dto.getPassword(), dto.getName(), dto.getPartname(), dto.getAuthority()
            );
        }
        catch(DuplicateKeyException e)
        {
            return 0;
        }
    }

    @Override
    public UserDTO selectUser(String userid, String password)
    {
        String strQuery = "select * from user where userid=? and password=password(?)";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new Object[]{userid, password}, new BeanPropertyRowMapper<>(UserDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public UserDTO selectUserFromUserid(String userid)
    {
        String strQuery = "select * from user where userid=?";

        try
        {
            return jdbcTemplate.queryForObject(strQuery, new Object[]{userid}, new BeanPropertyRowMapper<>(UserDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public List<UserDTO> selectAll()
    {
        String strQuery = "select * from user";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(UserDTO.class));
    }

    @Override
    public int update(int authority, UserDTO dto)
    {
        String strQuery;
        if(authority == CommonValue.ADMIN)
        {
            strQuery = "update user set partname=?, authority=? where userid=?";
            return jdbcTemplate.update(
                    strQuery,
                    dto.getPartname(), dto.getAuthority(), dto.getUserid()
            );
        }
        else
        {
            strQuery = "update user set partname=?, where userid=?";
            return jdbcTemplate.update(
                    strQuery,
                    dto.getPartname(), dto.getUserid()
            );
        }
    }

    @Override
    public int updatePw(String userid, String password)
    {
        String strQuery = "update user set password=password(?) where userid=?";
        return jdbcTemplate.update(strQuery, password, userid);
    }

    @Override
    public int delete(String userid)
    {
        String strQuery = "delete from user where userid=?";
        return jdbcTemplate.update(strQuery, userid);
    }
}
