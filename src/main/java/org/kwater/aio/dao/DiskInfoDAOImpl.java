package org.kwater.aio.dao;

import org.kwater.aio.dto.DiskInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DiskInfoDAOImpl implements IDiskInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(DiskInfoDTO dto)
    {
        String strQuery = "insert into disk_info values (?, ?, ?)";
        return jdbcTemplate.update(strQuery, dto.getHostname(), dto.getModel(), dto.getSize());
    }

    @Override
    public List<DiskInfoDTO> select(String hostname) {
        String strQuery = "select * from disk_info where hostname=?";
        return jdbcTemplate.query(strQuery, new Object[]{hostname}, new BeanPropertyRowMapper<>(DiskInfoDTO.class));
    }

    @Override
    public int update(DiskInfoDTO dto) {
        String strQuery = "update disk_info set size=? where hostname=? and model=?";
        return jdbcTemplate.update(strQuery, dto.getSize(), dto.getHostname(), dto.getModel());
    }
}
