package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiSedimentationInterfaceRealtimeDTO;
import org.kwater.aio.ai_dto.AiSedimentationRealtimeDTO;
import org.kwater.aio.ai_dto.FrequencyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiSedimentationRealtimeDAOImpl implements IAiSedimentationRealtimeDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Override
    public List<AiSedimentationRealtimeDTO> select(Date start_time, Date end_time)
    {
        String strQuery = "SELECT * FROM ai_sedimentation_realtime " +
                "WHERE update_time > ? AND update_time <= ? ORDER BY update_time";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiSedimentationRealtimeDTO.class)
        );
    }

    @Override
    public List<FrequencyDTO> selectE1Tb(Date start_time)
    {
        String strQuery = "select truncate(TBI_2001, 2) as value, count(truncate(TBI_2001, 2)) as count " +
                "from ai_sedimentation_realtime where update_time > ? group by truncate(TBI_2001, 2)";
        return jdbcTemplate.query(strQuery, new Object[]{start_time}, new BeanPropertyRowMapper<>(FrequencyDTO.class));
    }

    @Override
    public List<FrequencyDTO> selectE2Tb(Date start_time)
    {
        String strQuery = "select truncate(TBI_2002, 2) as value, count(truncate(TBI_2002, 2)) as count " +
                "from ai_sedimentation_realtime where update_time > ? group by truncate(TBI_2002, 2)";
        return jdbcTemplate.query(strQuery, new Object[]{start_time}, new BeanPropertyRowMapper<>(FrequencyDTO.class));
    }

    @Override
    public List<FrequencyDTO> selectDistribution(Date start_time, String name)
    {
        String strQuery = "select truncate(value, 2) as value, count(truncate(value, 2)) as count " +
                "FROM sedimentation_realtime where update_time > ? and name = ? group by truncate(value, 2)";
        return jdbcTemplate.query(strQuery, new Object[]{start_time, name}, new BeanPropertyRowMapper<>(FrequencyDTO.class));
    }

    @Override
    public List<AiSedimentationInterfaceRealtimeDTO> selectInterface(Date start_time, Date end_time)
    {
        String strQuery = "SELECT update_time, AIE_9901, AIE_9902 FROM ai_sedimentation_realtime " +
                "WHERE update_time > ? AND update_time <= ? ORDER BY update_time";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{start_time, end_time},
                new BeanPropertyRowMapper<>(AiSedimentationInterfaceRealtimeDTO.class)
        );
    }

    @Override
    public AiSedimentationRealtimeDTO select()
    {
        String strQuery = "SELECT * FROM ai_sedimentation_realtime ORDER BY update_time DESC LIMIT 1";
        try
        {
            return jdbcTemplate.queryForObject(strQuery, new BeanPropertyRowMapper<>(AiSedimentationRealtimeDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }

    }

    @Override
    public int delete(Date update_time)
    {
        String strQuery = "DELETE FROM ai_sedimentation_realtime WHERE update_time < ?";
        return jdbcTemplate.update(strQuery, update_time);
    }
}
