package org.kwater.aio.dao;

import org.kwater.aio.dto.SystemMonitoringDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class SystemMonitoringDAOImpl implements ISystemMonitoringDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(SystemMonitoringDTO dto)
    {
        String strQuery = "insert into system_monitoring (hostname, type, name, value, update_time)" +
                "values (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getHostname(), dto.getType(), dto.getName(), dto.getValue(), dto.getUpdate_time()
        );
    }

    @Override
    public List<SystemMonitoringDTO> select()
    {
        String strQuery = "select * from system_monitoring order by monitoring_index desc limit 1000";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(SystemMonitoringDTO.class));
    }

    @Override
    public List<SystemMonitoringDTO> select(String hostname)
    {
        String strQuery = "select * from system_monitoring where hostname=? order by monitoring_index desc limit 1000";
        return jdbcTemplate.query(strQuery, new Object[]{hostname}, new BeanPropertyRowMapper<>(SystemMonitoringDTO.class));
    }

    @Override
    public List<SystemMonitoringDTO> selectLatest(Date startDate)
    {
        String strQuery = "select * " +
                "from " +
                "(select * from system_monitoring where update_time > ? order by monitoring_index desc limit 1000) " +
                "system_monitoring " +
                "group by hostname, type, name";
        return jdbcTemplate.query(strQuery, new Object[]{startDate}, new BeanPropertyRowMapper<>(SystemMonitoringDTO.class));
    }

    @Override
    public int delete(Date date)
    {
        String strQuery = "delete from system_monitoring where update_time < ?";
        return jdbcTemplate.update(strQuery, date);
    }
}
