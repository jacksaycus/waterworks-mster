package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiCoagulantRealtimeDTO;

import java.util.Date;
import java.util.List;

public interface IAiCoagulantRealtimeDAO
{
    List<AiCoagulantRealtimeDTO> select(Date start_time, Date end_time);
    AiCoagulantRealtimeDTO select();
    int delete(Date update_time);
}
