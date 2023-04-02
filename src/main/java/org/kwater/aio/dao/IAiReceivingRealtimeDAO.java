package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiReceivingRealtimeDTO;

import java.util.Date;
import java.util.List;


public interface IAiReceivingRealtimeDAO
{
    List<AiReceivingRealtimeDTO> select(Date start_time, Date end_time);
    AiReceivingRealtimeDTO select();
    int delete(Date update_time);
}
