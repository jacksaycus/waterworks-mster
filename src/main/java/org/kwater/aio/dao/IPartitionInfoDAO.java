package org.kwater.aio.dao;

import org.kwater.aio.dto.PartitionInfoDTO;

import java.util.List;

public interface IPartitionInfoDAO
{
    int insert(PartitionInfoDTO dto);
    List<PartitionInfoDTO> select(String hostname);
    int update(PartitionInfoDTO dto);
}
