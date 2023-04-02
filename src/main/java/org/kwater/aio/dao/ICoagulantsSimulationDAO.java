package org.kwater.aio.dao;

import org.kwater.aio.dto.CoagulantsSimulationDTO;

import java.util.List;

public interface ICoagulantsSimulationDAO
{
    int insert(CoagulantsSimulationDTO dto);
    List<CoagulantsSimulationDTO> select();
    List<CoagulantsSimulationDTO> select(boolean upper, int state);
}
