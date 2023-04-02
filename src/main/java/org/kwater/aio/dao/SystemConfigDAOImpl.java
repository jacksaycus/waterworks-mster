package org.kwater.aio.dao;

import org.kwater.aio.dto.SystemConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SystemConfigDAOImpl implements ISystemConfigDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public SystemConfigDTO select()
    {
        String strQuery = "select * from system_config limit 1";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(SystemConfigDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public int update(SystemConfigDTO dto)
    {
        String strQuery = "update system_config set " +
                "scada1_address=?, scada1_port=?, daq1_port=?, scada2_address=?, scada2_port=?, daq2_port=?, " +
                "analysis1_address=?, analysis1_rm=?, analysis1_nm=?, analysis1_nn=?, " +
                "analysis2_address=?, analysis2_rm=?, analysis2_nm=?, analysis2_nn=?";

        return jdbcTemplate.update(
                strQuery,
                dto.getScada1_address(), dto.getScada1_port(), dto.getDaq1_port(),
                dto.getScada2_address(), dto.getScada2_port(), dto.getDaq2_port(),
                dto.getAnalysis1_address(), dto.getAnalysis1_rm(), dto.getAnalysis1_nm(), dto.getAnalysis1_nn(),
                dto.getAnalysis2_address(), dto.getAnalysis2_rm(), dto.getAnalysis2_nm(), dto.getAnalysis2_nn()
        );
    }
}
