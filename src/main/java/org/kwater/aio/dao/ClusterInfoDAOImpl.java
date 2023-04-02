package org.kwater.aio.dao;

import org.kwater.aio.dto.ClusterInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClusterInfoDAOImpl implements IClusterInfoDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<ClusterInfoDTO> select()
    {
        String strQuery = "select ci.cluster_id, wpi.shortname as water_purification, " +
                "ci.tb_min, ci.tb_avg, ci.tb_max, ci.ph_min, ci.ph_avg, ci.ph_max, " +
                "ci.te_min, ci.te_avg, ci.te_max, ci.cu_min, ci.cu_avg, ci.cu_max " +
                "from cluster_info as ci " +
                "join water_purification_info as wpi on ci.water_purification_code = wpi.code";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(ClusterInfoDTO.class));
    }
}
