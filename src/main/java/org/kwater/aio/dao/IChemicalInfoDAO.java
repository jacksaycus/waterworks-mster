package org.kwater.aio.dao;

import org.kwater.aio.dto.ChemicalInfoDTO;

import java.util.List;

public interface IChemicalInfoDAO
{
    int insert(ChemicalInfoDTO dto);
    List<ChemicalInfoDTO> select();
    ChemicalInfoDTO select(String code);
    int update(ChemicalInfoDTO dto);
    int delete(String code);
}
