package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiProcessControlDTO;

import java.util.Date;
import java.util.List;

public interface IAiProcessControlDAO
{
    AiProcessControlDTO select(Date update_time, Date run_time, String name, int kafka_flag);
    List<AiProcessControlDTO> select(Date run_time, int kafka_flag);
    int updateKafkaFlag(Date update_time, Date run_time, String name, int kafka_flag);
    int delete(Date date);
}
