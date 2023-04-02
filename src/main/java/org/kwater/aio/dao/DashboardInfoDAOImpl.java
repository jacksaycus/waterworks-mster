package org.kwater.aio.dao;

import org.kwater.aio.dto.DashboardDataDTO;
import org.kwater.aio.dto.DashboardIdDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardInfoDAOImpl implements IDashboardInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(String data)
    {
        String strQuery = "insert into dashboard_info values (null, ?)";
        return jdbcTemplate.update(
                strQuery,
                data
        );
    }

    @Override
    public DashboardIdDTO selectLatest()
    {
        String strQuery = "select dashboard_id from dashboard_info order by dashboard_id desc limit 1";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(DashboardIdDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public DashboardDataDTO select(int dashboard_id)
    {
        String strQuery = "select data from dashboard_info where dashboard_id = ?";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new Object[]{dashboard_id}, new BeanPropertyRowMapper<>(DashboardDataDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int update(int dashboard_id, String data)
    {
        String strQuery = "update dashboard_info set data = ? where dashboard_id = ?";
        return jdbcTemplate.update(strQuery, data, dashboard_id);
    }

    @Override
    public int delete(int dashboard_id)
    {
        String strQuery = "delete from dashboard_info where dashboard_id = ?";
        return jdbcTemplate.update(strQuery, dashboard_id);
    }
}
