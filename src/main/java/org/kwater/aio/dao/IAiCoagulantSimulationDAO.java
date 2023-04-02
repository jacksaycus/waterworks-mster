package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiCoagulantSimulationDTO;

import java.util.Date;
import java.util.List;

public interface IAiCoagulantSimulationDAO
{
    int insert(AiCoagulantSimulationDTO dto);
    List<AiCoagulantSimulationDTO> select(Date start_time, Date end_time);
}
