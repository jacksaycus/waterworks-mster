package org.kwater.aio.dao;

import org.kwater.aio.dto.ProcessRealtimeDTO;

import java.util.Date;
import java.util.List;

public interface IProcessRealtimeDAO
{
    int insert(List<ProcessRealtimeDTO> list);
    List<ProcessRealtimeDTO> select(Date start_time);
    List<ProcessRealtimeDTO> select(String name, Date start_time, Date end_time);
    List<ProcessRealtimeDTO> select(String partitionName);
    ProcessRealtimeDTO selectLatest(String name);
    void addPartition(String partitionName, String end_time);
    void dropPartition(String partitionName);
}
