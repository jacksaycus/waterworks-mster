package org.kwater.aio.dao;

import org.kwater.aio.dto.AlarmInfoDTO;

import java.util.List;

public interface IAlarmInfoDAO
{
    int insert(AlarmInfoDTO dto);
    List<AlarmInfoDTO> select();
    int update(AlarmInfoDTO dto);
    int delete(int alarm_id);
}
