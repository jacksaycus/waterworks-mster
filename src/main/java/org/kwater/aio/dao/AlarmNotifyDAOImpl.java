package org.kwater.aio.dao;

import org.kwater.aio.dto.AlarmNotifyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AlarmNotifyDAOImpl implements IAlarmNotifyDAO
{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int insert(AlarmNotifyDTO dto)
    {
        String strQuery = "insert into alarm_notify values (null, ?, ?, ?, ?, 0)";
        return jdbcTemplate.update(
                strQuery,
                dto.getAlarm_id(), dto.getAlarm_time(), dto.getHostname(), dto.getValue()/*, dto.isAck_state()*/ // 알람 네비게이터 삭제
        );
    }

    @Override
    public List<AlarmNotifyDTO> select()
    {
        // 알람 네비게이터 삭제
//        String strQuery = "select " +
//                "alarm_notify_index, alarm_id, alarm_time, hostname, value, IF(ack_state, 'true', 'false') as ack_state " +
//                "from alarm_notify order by alarm_notify_index desc limit 1000";
        String strQuery = "select " +
                "alarm_notify_index, alarm_id, alarm_time, hostname, value " +
                "from alarm_notify order by alarm_notify_index desc limit 1000";
        return jdbcTemplate.query(strQuery, new BeanPropertyRowMapper<>(AlarmNotifyDTO.class));
    }

    @Override
    public List<AlarmNotifyDTO> select(Date start_time)
    {
        String strQuery = "select alarm_notify_index, alarm_id, alarm_time, hostname, value " +
                "from alarm_notify where alarm_time >= ? and ack_state = 0 " +
                "order by alarm_notify_index desc limit 10";
        return jdbcTemplate.query(strQuery, new Object[]{start_time}, new BeanPropertyRowMapper<>(AlarmNotifyDTO.class));
    }

    @Override
    public List<AlarmNotifyDTO> select(boolean ackState)
    {
        // 알람 네비게이터 삭제
//        String strQuery = "select " +
//                "alarm_notify_index, alarm_id, alarm_time, hostname, value, IF(ack_state, 'true', 'false') as ack_state " +
//                "from alarm_notify where ack_state=? " +
//                "order by alarm_notify_index desc limit 1000";
        String strQuery = "select " +
                "alarm_notify_index, alarm_id, alarm_time, hostname, value " +
                "from alarm_notify where ack_state=? " +
                "order by alarm_notify_index desc limit 1000";
        return jdbcTemplate.query(strQuery, new Object[]{ackState}, new BeanPropertyRowMapper<>(AlarmNotifyDTO.class));
    }

    @Override
    public AlarmNotifyDTO select(int alarm_id, Date alarm_time, String hostname)
    {
        // 알람 네비게이터 삭제
//        String strQuery = "select " +
//                "alarm_notify_index, alarm_id, alarm_time, hostname, value, IF(ack_state, 'true', 'false') as ack_state " +
//                "from alarm_notify where alarm_id=? and hostname=? and alarm_time>?" +
//                "order by alarm_notify_index desc limit 1";
        String strQuery = "select " +
                "alarm_notify_index, alarm_id, alarm_time, hostname, value " +
                "from alarm_notify where alarm_id=? and hostname=? and alarm_time>?" +
                "order by alarm_notify_index desc limit 1";
        try
        {
            return jdbcTemplate.queryForObject(
                    strQuery,
                    new Object[]{alarm_id, hostname, alarm_time},
                    new BeanPropertyRowMapper<>(AlarmNotifyDTO.class));
        }
        catch(EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public List<AlarmNotifyDTO> select(Date start_time, Date end_time)
    {
// 알람 네비게이터 삭제
//        String strQuery = "select " +
//                "alarm_notify_index, alarm_id, alarm_time, hostname, value, IF(ack_state, 'true', 'false') as ack_state " +
//                "from alarm_notify where alarm_time >= ? and alarm_time < ?" +
//                "order by alarm_notify_index desc";
        String strQuery = "select " +
                "alarm_notify_index, alarm_id, alarm_time, hostname, value " +
                "from alarm_notify where alarm_time >= ? and alarm_time < ?" +
                "order by alarm_notify_index desc";
        return jdbcTemplate.query(strQuery, new Object[]{start_time, end_time}, new BeanPropertyRowMapper<>(AlarmNotifyDTO.class));
    }

    @Override
    public int updateAckState(int alarmNotifyIndex, boolean ackState)
    {
        String strQuery = "update alarm_notify set ack_state=? where alarm_notify_index=?";
        return jdbcTemplate.update(strQuery, ackState, alarmNotifyIndex);
    }
}
