package org.kwater.aio.dao;

import org.kwater.aio.dto.SystemConfigDTO;

public interface ISystemConfigDAO
{
    SystemConfigDTO select();
    int update(SystemConfigDTO dto);
}
