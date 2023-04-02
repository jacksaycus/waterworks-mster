package org.kwater.aio.dao;

import org.kwater.aio.dto.TagDescriptionDTO;

import java.util.List;

public interface ITagDescriptionDAO
{
    int insert(TagDescriptionDTO dto);
    List<TagDescriptionDTO> select();
    int update(TagDescriptionDTO dto);
    int delete(int tagIndex);
}
