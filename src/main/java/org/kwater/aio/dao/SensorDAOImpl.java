package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.FrequencyDTO;
import org.kwater.aio.dto.SensorDTO;
import org.kwater.aio.dto.TrendCodeDTO;
import org.kwater.aio.dto.TrendTbDTO;
import org.kwater.aio.resource_dto.CoagulantsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class SensorDAOImpl implements ISensorDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(SensorDTO dto)
    {
        String strQuery = "insert into sensor " +
                "values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(
                strQuery,
                dto.getUpdate_time(), dto.getPd1_diatom(), dto.getPd2_diatom(), dto.getPd3_diatom(),
                dto.getPd1_diatom_big(), dto.getPd2_diatom_big(), dto.getPd3_diatom_big(), dto.getPd_diatom_avg(),
                dto.getPd_toc(), dto.getPd_ch(), dto.getSj_al(),
                dto.getHs_tb(), dto.getHs_ph(), dto.getHs_te(), dto.getHs_cu(),
                dto.getHs_e1_tb(), dto.getHs_e2_tb(), dto.getHs_f_tb(),
                dto.getAi_coagulants1_input(), dto.getAi_coagulants2_input(),
                dto.getAi_coagulants1_type(), dto.getAi_coagulants2_type(),
                dto.getScl_coagulants1_input(), dto.getScl_coagulants2_input(),
                dto.getScl_coagulants1_type(), dto.getScl_coagulants2_type(),
                dto.getUser_coagulants1_input(), dto.getUser_coagulants2_input(),
                dto.getUser_coagulants1_type(), dto.getUser_coagulants2_type(),
                dto.getReal_coagulants1_input(), dto.getReal_coagulants2_input(),
                dto.getReal_coagulants1_type(), dto.getReal_coagulants2_type(),
                dto.getOperation1_mode(), dto.getOperation2_mode()
        );
    }

    @Override
    public List<SensorDTO> select(Date start_time)
    {
        String strQuery = "select * from sensor where update_time > ? order by sensor_index desc";
        return jdbcTemplate.query(strQuery, new Object[]{start_time}, new BeanPropertyRowMapper<>(SensorDTO.class));
    }

    @Override
    public SensorDTO selectLatest()
    {
        String strQuery = "select * from sensor order by sensor_index desc limit 1";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(SensorDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public List<CoagulantsDTO> selectHistory()
    {
        String strQuery = "select sensor_index as coagulants_index, update_time, " +
                "ai_coagulants1_input, ai_coagulants2_input, ai_coagulants1_type, ai_coagulants2_type, " +
                "scl_coagulants1_input, scl_coagulants2_input, scl_coagulants1_type, scl_coagulants2_type, " +
                "user_coagulants1_input, user_coagulants2_input, user_coagulants1_type, user_coagulants2_type, " +
                "urea_coagulants1_input, real_coagulants2_input, real_coagulants1_type, real_coagulants2_type, " +
                "operation1_mode, operation2_mode " +
                "from sensor order by sensor_index desc limit 1000";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(CoagulantsDTO.class));
    }

    @Override
    public CoagulantsDTO selectCoagulants()
    {
        String strQuery = "select sensor_index as coagulants_index, update_time, " +
                "ai_coagulants1_input, ai_coagulants2_input, ai_coagulants1_type, ai_coagulants2_type, " +
                "scl_coagulants1_input, scl_coagulants2_input, scl_coagulants1_type, scl_coagulants2_type, " +
                "user_coagulants1_input, user_coagulants2_input, user_coagulants1_type, user_coagulants2_type, " +
                "urea_coagulants1_input, real_coagulants2_input, real_coagulants1_type, real_coagulants2_type, " +
                "operation1_mode, operation2_mode " +
                "from sensor order by sensor_index desc limit 1";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(CoagulantsDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public List<TrendTbDTO> selectTb(Date start_time, Date end_time)
    {
        String strQuery = "select update_time, hs_e1_tb, hs_e2_tb, hs_f_tb from sensor " +
                "where update_time >= ? and update_time < ? order by update_time desc";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(TrendTbDTO.class)
        );
    }

    @Override
    public List<TrendCodeDTO> selectCode(String code, Date start_time, Date end_time)
    {
//        String strQuery = "select update_time, hs_f_tb as code from sensor " +
//                "where update_time >= ? and update_time < ? order by update_time desc";
        String strQuery = String.format("select update_time, %s as code from sensor " +
                "where update_time >= ? and update_time < ? order by update_time desc", code);
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(TrendCodeDTO.class)
        );
    }

    @Override
    public List<FrequencyDTO> selectDistinctCountHsE1Tb(Date start_time)
    {
        String strQuery = "select truncate(hs_e1_tb, 2) as value, count(truncate(hs_e1_tb, 2)) as count " +
                "from sensor where update_time > ? group by truncate(hs_e1_tb, 2)";
        return jdbcTemplate.query(strQuery, new Object[]{start_time}, new BeanPropertyRowMapper<>(FrequencyDTO.class));
    }

    @Override
    public List<FrequencyDTO> selectDistinctCountHsE2Tb(Date start_time)
    {
        String strQuery = "select truncate(hs_e2_tb, 2) as value, count(truncate(hs_e2_tb, 2)) as count " +
                "from sensor where update_time > ? group by truncate(hs_e2_tb, 2)";
        return jdbcTemplate.query(strQuery, new Object[]{start_time}, new BeanPropertyRowMapper<>(FrequencyDTO.class));
    }

    @Override
    public List<FrequencyDTO> selectDistinctCountHsFTb(Date start_time)
    {
        String strQuery = "select truncate(hs_f_tb, 2) as value, count(truncate(hs_f_tb, 2)) as count " +
                "from sensor where update_time > ? group by truncate(hs_f_tb, 2)";
        return jdbcTemplate.query(strQuery, new Object[]{start_time}, new BeanPropertyRowMapper<>(FrequencyDTO.class));
    }

    @Override
    public int delete(Date update_time)
    {
        String strQuery = "delete from sensor where update_time < ?";
        return jdbcTemplate.update(strQuery, update_time);
    }
}
