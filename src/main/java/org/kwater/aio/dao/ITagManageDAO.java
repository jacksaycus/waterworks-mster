package org.kwater.aio.dao;

import org.kwater.aio.dto.TagManageDTO;
import org.kwater.aio.dto.TagManageRangeDTO;

import java.util.List;

public interface ITagManageDAO
{
    int insert(TagManageDTO dto);
    List<TagManageDTO> select();
    List<TagManageDTO> select(int type);
    List<TagManageDTO> select(String process);
    TagManageRangeDTO selectRange(String process);
    int update(TagManageDTO dto);
    int delete(TagManageDTO dto);
}
