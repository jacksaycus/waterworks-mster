package org.kwater.aio.dao;

import org.kwater.aio.dto.AlarmNotifyDTO;

import java.util.Date;
import java.util.List;

public interface IAlarmNotifyDAO
{
    int insert(AlarmNotifyDTO dto);
    List<AlarmNotifyDTO> select();
    List<AlarmNotifyDTO> select(Date start_time);
    List<AlarmNotifyDTO> select(boolean ackState);
    AlarmNotifyDTO select(int alarm_id, Date alarm_time, String hostname);
    List<AlarmNotifyDTO> select(Date start_time, Date end_time);
    int updateAckState(int alarmNotifyIndex, boolean ackState);
}
