package org.kwater.aio.dao;

import org.kwater.aio.dto.ChemicalInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChemicalInfoDAOImpl implements IChemicalInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(ChemicalInfoDTO dto)
    {
        String strQuery = "insert into chemical_info values (?, ?, ?)";
        return jdbcTemplate.update(strQuery, dto.getCode(), dto.getShortname(), dto.getFullname());
    }

    @Override
    public List<ChemicalInfoDTO> select()
    {
        String strQuery = "select * from chemical_info";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(ChemicalInfoDTO.class));
    }

    @Override
    public ChemicalInfoDTO select(String code)
    {
        String strQuery = "select * from chemical_info where code=?";
        try
        {
            return jdbcTemplate.queryForObject(
                    strQuery,
                    new Object[]{code},
                    new BeanPropertyRowMapper<>(ChemicalInfoDTO.class)
            );
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int update(ChemicalInfoDTO dto)
    {
        String strQuery = "update chemical_info set shortname=?, fullname=? where code=?";
        return jdbcTemplate.update(strQuery, dto.getShortname(), dto.getFullname(), dto.getCode());
    }

    @Override
    public int delete(String code)
    {
        String strQuery = "delete from chemical_info where code=?";
        return jdbcTemplate.update(strQuery, code);
    }
}
