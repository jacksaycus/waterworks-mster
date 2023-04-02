package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiDisinfectionRealtimeDTO;

import java.util.Date;
import java.util.List;

public interface IAiDisinfectionRealtimeDAO
{
    List<AiDisinfectionRealtimeDTO> select(Date start_time, Date end_time);
    AiDisinfectionRealtimeDTO select();
    int delete(Date update_time);
}
