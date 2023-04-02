package org.kwater.aio.dao;

import org.kwater.aio.dto.InterfaceInfoDTO;

import java.util.List;

public interface IInterfaceInfoDAO
{
    int insert(InterfaceInfoDTO dto);
    List<InterfaceInfoDTO> select(String hostname);
    List<InterfaceInfoDTO> selectWhereAddress(String address);
    int update(InterfaceInfoDTO dto);
}
