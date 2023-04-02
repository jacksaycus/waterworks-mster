package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiSedimentationInterfaceRealtimeDTO;
import org.kwater.aio.ai_dto.AiSedimentationRealtimeDTO;
import org.kwater.aio.ai_dto.FrequencyDTO;

import java.util.Date;
import java.util.List;

public interface IAiSedimentationRealtimeDAO
{
    List<AiSedimentationRealtimeDTO> select(Date start_time, Date end_time);
    List<FrequencyDTO> selectE1Tb(Date start_time);
    List<FrequencyDTO> selectE2Tb(Date start_time);
    List<FrequencyDTO> selectDistribution(Date start_time, String name);
    List<AiSedimentationInterfaceRealtimeDTO> selectInterface(Date start_time, Date end_time);
    AiSedimentationRealtimeDTO select();
    int delete(Date update_time);
}
