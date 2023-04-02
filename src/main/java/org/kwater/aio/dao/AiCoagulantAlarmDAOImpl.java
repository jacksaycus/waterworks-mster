package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiProcessAlarmDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AiCoagulantAlarmDAOImpl implements IAiProcessAlarmDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(int alarm_id, Date alarm_time, int kafka_flag)
    {
        String strQuery = "insert into ai_coagulant_alarm values (?, ?, ?)";
        return jdbcTemplate.update(strQuery, alarm_id, alarm_time, kafka_flag);
    }

    @Override
    public List<AiProcessAlarmDTO> select(Date alarm_time, int kafka_flag)
    {
        String strQuery = "SELECT * FROM ai_coagulant_alarm WHERE alarm_time>=? AND kafka_flag=? ORDER BY alarm_time DESC";
        return jdbcTemplate.query(
                strQuery,
                new Object[]{alarm_time, kafka_flag},
                new BeanPropertyRowMapper<>(AiProcessAlarmDTO.class)
        );
    }

    @Override
    public int updateKafkaFlag(int alarm_id, Date alarm_time, int kafka_flag)
    {
        String strQuery = "UPDATE ai_coagulant_alarm SET kafka_flag=? WHERE alarm_id=? AND alarm_time=?";
        return jdbcTemplate.update(strQuery, kafka_flag, alarm_id, alarm_time);
    }

    @Override
    public int delete(Date date)
    {
        String strQuery = "DELETE FROM ai_coagulant_alarm WHERE alarm_time<?";
        return jdbcTemplate.update(strQuery, date);
    }
}
