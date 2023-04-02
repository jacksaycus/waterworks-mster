package org.kwater.aio.dao;

import org.kwater.aio.dto.DiskInfoDTO;

import java.util.List;

public interface IDiskInfoDAO
{
    int insert(DiskInfoDTO dto);
    List<DiskInfoDTO> select(String hostname);
    int update(DiskInfoDTO dto);
}
