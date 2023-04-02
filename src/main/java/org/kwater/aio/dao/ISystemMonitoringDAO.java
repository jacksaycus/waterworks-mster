package org.kwater.aio.dao;

import org.kwater.aio.dto.SystemMonitoringDTO;

import java.util.Date;
import java.util.List;

public interface ISystemMonitoringDAO
{
    int insert(SystemMonitoringDTO dto);
    List<SystemMonitoringDTO> select();
    List<SystemMonitoringDTO> select(String hostname);
    List<SystemMonitoringDTO> selectLatest(Date startDate);
    int delete(Date date);
}
