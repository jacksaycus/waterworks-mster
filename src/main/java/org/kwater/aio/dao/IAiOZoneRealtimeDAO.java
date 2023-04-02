package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiOzoneRealtimeDTO;

import java.util.Date;
import java.util.List;

public interface IAiOZoneRealtimeDAO
{
    List<AiOzoneRealtimeDTO> select(Date start_time, Date end_time);
    AiOzoneRealtimeDTO select();
    int delete(Date update_time);
}
