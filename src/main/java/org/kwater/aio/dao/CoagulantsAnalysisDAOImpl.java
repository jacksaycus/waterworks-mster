package org.kwater.aio.dao;

import org.kwater.aio.dto.CoagulantsAnalysisDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class CoagulantsAnalysisDAOImpl implements ICoagulantsAnalysisDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<CoagulantsAnalysisDTO> select()
    {
        String strQuery = "select ca.log_time, ca.start_time, ca.end_time, ca.reg_time, " +
                "wpi.shortname as water_purification, ca.cluster_id, " +
                "ci1.shortname as chemical1, ca.injection1_percent, ca.injection1_ai, ca.injection1_revision, " +
                "ca.injection1_amount, ca.d1_fr, " +
                "ci2.shortname as chemical2, ca.injection2_percent, ca.injection2_ai, ca.injection2_revision, " +
                "ca.injection2_amount, ca.d2_fr, " +
                "ca.tb, ca.ph, ca.te, ca.cu, " +
                "ca.total_count, ca.collect_count, ca.available_count, " +
                "ca.error_count, ca.missing_count from coagulants_analysis as ca " +
                "join water_purification_info as wpi on ca.water_purification_code = wpi.code " +
                "join chemical_info as ci1 on ca.chemical1_code = ci1.code " +
                "join chemical_info as ci2 on ca.chemical2_code = ci2.code " +
                "where ca.end_time > date_add(now(), INTERVAL -1 MONTH) " +
                "order by ca.log_time desc";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(CoagulantsAnalysisDTO.class));
    }

    @Override
    public CoagulantsAnalysisDTO select(Date log_time)
    {
        String strQuery = "select ca.log_time, ca.start_time, ca.end_time, ca.reg_time, " +
                "wpi.shortname as water_purification, ca.cluster_id, " +
                "ci1.shortname as chemical1, ca.injection1_percent, ca.injection1_ai, ca.injection1_revision, " +
                "ca.injection1_amount, ca.d1_fr, " +
                "ci2.shortname as chemical2, ca.injection2_percent, ca.injection2_ai, ca.injection2_revision, " +
                "ca.injection2_amount, ca.d2_fr, " +
                "ca.tb, ca.ph, ca.te, ca.cu, " +
                "ca.total_count, ca.collect_count, ca.available_count, " +
                "ca.error_count, ca.missing_count from coagulants_analysis as ca " +
                "join water_purification_info as wpi on ca.water_purification_code = wpi.code " +
                "join chemical_info as ci1 on ca.chemical1_code = ci1.code " +
                "join chemical_info as ci2 on ca.chemical2_code = ci2.code " +
                "where log_time=? " +
                "order by ca.log_time desc limit 1";
        try
        {
            return jdbcTemplate.queryForObject(
                    strQuery,
                    new Object[]{log_time},
                    new BeanPropertyRowMapper<>(CoagulantsAnalysisDTO.class)
            );
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public CoagulantsAnalysisDTO selectLatest()
    {
        String strQuery = "select ca.log_time, ca.start_time, ca.end_time, ca.reg_time, " +
                "wpi.shortname as water_purification, ca.cluster_id, " +
                "ci1.shortname as chemical1, ca.injection1_percent, ca.injection1_ai, ca.injection1_revision, " +
                "ca.injection1_amount, ca.d1_fr, " +
                "ci2.shortname as chemical2, ca.injection2_percent, ca.injection2_ai, ca.injection2_revision, " +
                "ca.injection2_amount, ca.d2_fr, " +
                "ca.tb, ca.ph, ca.te, ca.cu, " +
                "ca.total_count, ca.collect_count, ca.available_count, " +
                "ca.error_count, ca.missing_count from coagulants_analysis as ca " +
                "join water_purification_info as wpi on ca.water_purification_code = wpi.code " +
                "join chemical_info as ci1 on ca.chemical1_code = ci1.code " +
                "join chemical_info as ci2 on ca.chemical2_code = ci2.code " +
                "order by ca.log_time desc limit 1";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(CoagulantsAnalysisDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public List<CoagulantsAnalysisDTO> select2Latest()
    {
        String strQuery = "select ca.log_time, ca.start_time, ca.end_time, ca.reg_time, " +
                "wpi.shortname as water_purification, ca.cluster_id, " +
                "ci1.shortname as chemical1, ca.injection1_percent, ca.injection1_ai, ca.injection1_revision, " +
                "ca.injection1_amount, ca.d1_fr, " +
                "ci2.shortname as chemical2, ca.injection2_percent, ca.injection2_ai, ca.injection2_revision, ca.injection2_amount, ca.d2_fr, " +
                "ca.tb, ca.ph, ca.te, ca.cu, " +
                "ca.total_count, ca.collect_count, ca.available_count, " +
                "ca.error_count, ca.missing_count from coagulants_analysis as ca " +
                "join water_purification_info as wpi on ca.water_purification_code = wpi.code " +
                "join chemical_info as ci1 on ca.chemical1_code = ci1.code " +
                "join chemical_info as ci2 on ca.chemical2_code = ci2.code " +
                "order by ca.log_time desc limit 2";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(CoagulantsAnalysisDTO.class));
    }

    @Override
    public List<CoagulantsAnalysisDTO> selectMinute(Date start_time)
    {
        String strQuery = "select * from (" +
                "select ca.log_time, ca.start_time, ca.end_time, ca.reg_time, " +
                "wpi.shortname as water_purification, ca.cluster_id, " +
                "ci1.shortname as chemical1, ca.injection1_percent, ca.injection1_ai, ca.injection1_revision, " +
                "ca.injection1_amount, ca.d1_fr, " +
                "ci2.shortname as chemical2, ca.injection2_percent, ca.injection2_ai, ca_injection2_revision, " +
                "ca.injection2_amount, ca.d2_fr, " +
                "ca.tb, ca.ph, ca.te, ca.cu, " +
                "ca.total_count, ca.collect_count, ca.available_count, ca.error_count, ca.missing_count " +
                "from coagulants_analysis as ca " +
                "join water_purification_info as wpi on ca.water_purification_code = wpi.code " +
                "join chemical_info as ci1 on ca.chemical1_code = ci1.code " +
                "join chemical_info as ci2 on ca.chemical2_code = ci2.code " +
                "where log_time > ? order by ca.log_time desc)a " +
                "group by date_format(log_time, \"%Y-%M-%D %H:%i\")";
        return jdbcTemplate.query(strQuery, new Object[]{start_time}, new BeanPropertyRowMapper<>(CoagulantsAnalysisDTO.class));

        //return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(CoagulantsAnalysisDTO.class));
    }
}
