package org.kwater.aio.dao;

import org.kwater.aio.dto.InterfaceInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InterfaceInfoDAOImpl implements IInterfaceInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(InterfaceInfoDTO dto)
    {
        String strQuery = "insert into interface_info values (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getHostname(), dto.getName(), dto.getDisplay_name(), dto.getIpv4(), dto.getMac()
        );
    }

    @Override
    public List<InterfaceInfoDTO> select(String hostname)
    {
        String strQuery = "select * from interface_info where hostname=?";
        return jdbcTemplate.query(strQuery, new Object[]{hostname}, new BeanPropertyRowMapper<>(InterfaceInfoDTO.class));
    }

    @Override
    public List<InterfaceInfoDTO> selectWhereAddress(String address)
    {
        String strQuery = "select * from interface_info where ipv4=?";

        return jdbcTemplate.query(strQuery, new Object[]{address}, new BeanPropertyRowMapper<>(InterfaceInfoDTO.class));
    }

    @Override
    public int update(InterfaceInfoDTO dto)
    {
        String strQuery = "update interface_info set display_name=?, ipv4=?, mac=? where hostname=? and name=?";
        return jdbcTemplate.update(
                strQuery,
                dto.getDisplay_name(), dto.getIpv4(), dto.getMac(), dto.getHostname(), dto.getName()
                );
    }
}
