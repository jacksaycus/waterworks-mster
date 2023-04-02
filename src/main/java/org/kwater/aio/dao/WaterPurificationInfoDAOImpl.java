package org.kwater.aio.dao;

import org.kwater.aio.dto.WaterPurificationInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WaterPurificationInfoDAOImpl implements IWaterPurificationInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(WaterPurificationInfoDTO dto)
    {
        String strQuery = "insert into water_purification_info values (?, ?, ?)";
        return jdbcTemplate.update(strQuery, dto.getCode(), dto.getShortname(), dto.getFullname());
    }

    @Override
    public List<WaterPurificationInfoDTO> select()
    {
        String strQuery = "select * from water_purification_info";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(WaterPurificationInfoDTO.class));
    }

    @Override
    public WaterPurificationInfoDTO select(String code)
    {
        String strQuery = "select * from water_purification_info where code=?";
        try
        {
            return jdbcTemplate.queryForObject(
                    strQuery,
                    new Object[]{code},
                    new BeanPropertyRowMapper<>(WaterPurificationInfoDTO.class)
            );
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int update(WaterPurificationInfoDTO dto)
    {
        String strQuery = "update water_purification_info set shortname=?, fullname=? where code=?";
        return jdbcTemplate.update(strQuery, dto.getShortname(), dto.getFullname(), dto.getCode());
    }

    @Override
    public int delete(String code)
    {
        String strQuery = "delete from water_purification_info where code=?";
        return jdbcTemplate.update(strQuery, code);
    }
}
