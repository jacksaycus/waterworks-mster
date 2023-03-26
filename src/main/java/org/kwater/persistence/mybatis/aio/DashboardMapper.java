package org.kwater.persistence.mybatis.aio;

import org.kwater.domain.aio.AiProcessInitDTO;

public interface DashboardMapper {
    AiProcessInitDTO getAiReceivingInit(String item);
}
