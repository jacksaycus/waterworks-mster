package org.kwater.aio.dao;

import org.kwater.aio.ai_dto.AiProcessInitDTO;

import java.util.List;

public interface IAiProcessInitDAO
{
    List<AiProcessInitDTO> select();
    AiProcessInitDTO select(String item);
    int updateOperationMode(int operation_mode);
    int update(String item, float value);
}
