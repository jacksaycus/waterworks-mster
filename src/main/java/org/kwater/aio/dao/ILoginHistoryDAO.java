package org.kwater.aio.dao;

import org.kwater.aio.dto.LoginHistoryDTO;

import java.util.Date;
import java.util.List;

public interface ILoginHistoryDAO
{
    List<LoginHistoryDTO> select();
    int insert(LoginHistoryDTO dto);
    int delete(Date date);
}
