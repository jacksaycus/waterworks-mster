package org.kwater.aio.dao;

import org.kwater.aio.dto.ClassInfoDTO;

import java.util.List;

public interface IClassInfoDAO
{
    int insert(ClassInfoDTO dto);
    List<ClassInfoDTO> select();
    int update(ClassInfoDTO dto);
    int delete(int class_index);
}
