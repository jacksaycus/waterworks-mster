package org.kwater.aio.dao;

import org.kwater.aio.dto.WaterPurificationInfoDTO;

import java.util.List;

public interface IWaterPurificationInfoDAO
{
    int insert(WaterPurificationInfoDTO dto);
    List<WaterPurificationInfoDTO> select();
    WaterPurificationInfoDTO select(String code);
    int update(WaterPurificationInfoDTO dto);
    int delete(String code);
}
