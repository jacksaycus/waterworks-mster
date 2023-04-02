package org.kwater.aio.dao;

import org.kwater.aio.dto.SystemInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SystemInfoDAOImpl implements ISystemInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(SystemInfoDTO dto)
    {
        String strQuery = "insert into system_info values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getHostname(), dto.getHostname(), dto.getOs(), dto.getModel(),
                dto.getProcessor_name(), dto.getPackage_count(), dto.getCore_count(),
                dto.getLogical_count(), dto.getMax_frequency(),
                dto.getTotal_memory(), dto.getAvailable_memory(),
                dto.getDb_used(), dto.getDb_free(),
                dto.getUpdate_time()
        );
    }

    @Override
    public SystemInfoDTO select(String hostname)
    {
        String strQuery = "select * from system_info where hostname=?";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new Object[]{hostname}, new BeanPropertyRowMapper<>(SystemInfoDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public List<SystemInfoDTO> select()
    {
        String strQuery = "select * from system_info";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(SystemInfoDTO.class));
    }

    @Override
    public int update(SystemInfoDTO dto)
    {
        String strQuery = "update system_info " +
                "set os=?, model=?, processor_name=?, package_count=?, core_count=?, logical_count=?, max_frequency=?, " +
                "total_memory=?, available_memory=?, db_used=?, db_free=?, update_time=? " +
                "where hostname=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getOs(), dto.getModel(), dto.getProcessor_name(), dto.getPackage_count(),
                dto.getCore_count(), dto.getLogical_count(), dto.getMax_frequency(),
                dto.getTotal_memory(), dto.getAvailable_memory(), dto.getDb_used(), dto.getDb_free(), dto.getUpdate_time(),
                dto.getHostname()
        );
    }

    @Override
    public int updateName(String hostname, String name)
    {
        String strQuery = "update system_info set name=? where hostname=?";
        return jdbcTemplate.update(strQuery, name, hostname);
    }
}
