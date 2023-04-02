package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiProcessAlarmDTO;

import java.util.Date;
import java.util.List;

public interface IAiProcessAlarmDAO
{
    int insert(int alarm_id, Date alarm_time, int kafka_flag);
    List<AiProcessAlarmDTO> select(Date alarm_time, int kafka_flag);
    int updateKafkaFlag(int alarm_id, Date alarm_time, int kafka_flag);
    int delete(Date date);
}
