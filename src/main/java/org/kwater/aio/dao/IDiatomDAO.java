package org.kwater.aio.dao;

import org.kwater.aio.dto.DiatomDTO;

import java.util.Date;
import java.util.List;

public interface IDiatomDAO
{
    int insert(DiatomDTO dto);
    List<DiatomDTO> select();
    DiatomDTO selectLatest();
    int update(DiatomDTO dto);
    int delete(int diatom_index);
}
