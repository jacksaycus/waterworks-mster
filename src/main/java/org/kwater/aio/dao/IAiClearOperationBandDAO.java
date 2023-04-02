package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiClearOperationBandDTO;

import java.util.Date;
import java.util.List;

public interface IAiClearOperationBandDAO
{
    List<AiClearOperationBandDTO> select(Date start_time, Date end_time);
}
