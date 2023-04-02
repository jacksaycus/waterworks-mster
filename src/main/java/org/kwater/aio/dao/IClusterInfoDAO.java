package org.kwater.aio.dao;

import org.kwater.aio.dto.ClusterInfoDTO;

import java.util.List;

public interface IClusterInfoDAO
{
    List<ClusterInfoDTO> select();
}
