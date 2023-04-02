package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiMixingRealtimeDTO;

import java.util.Date;
import java.util.List;

public interface IAiMixingRealtimeDAO
{
    List<AiMixingRealtimeDTO> select(Date start_time, Date end_time);
    AiMixingRealtimeDTO select();
    int delete(Date update_time);
}
