package org.kwater.aio.dao;

import org.kwater.aio.dto.PartitionInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PartitionInfoDAOImpl implements IPartitionInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(PartitionInfoDTO dto)
    {
        String strQuery = "insert into partition_info values (?, ?, ?, ?)";
        return jdbcTemplate.update(strQuery, dto.getHostname(), dto.getName(), dto.getTotal_size(), dto.getUsable_size());
    }

    @Override
    public List<PartitionInfoDTO> select(String hostname)
    {
        String strQuery = "select * from partition_info where hostname=?";
        return jdbcTemplate.query(strQuery, new Object[]{hostname}, new BeanPropertyRowMapper<>(PartitionInfoDTO.class));
    }

    @Override
    public int update(PartitionInfoDTO dto)
    {
        String strQuery = "update partition_info set total_size=?, usable_size=? where hostname=? and name=?";
        return jdbcTemplate.update(strQuery, dto.getTotal_size(), dto.getUsable_size(), dto.getHostname(), dto.getName());
    }
}
