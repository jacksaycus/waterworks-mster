package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiFilterRealtimeDTO;

import java.util.Date;
import java.util.List;

public interface IAiFilterRealtimeDAO
{
    List<AiFilterRealtimeDTO> select(Date start_time, Date end_time);
    AiFilterRealtimeDTO select();
    int delete(Date update_time);
}
