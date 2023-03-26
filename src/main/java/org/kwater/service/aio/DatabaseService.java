package org.kwater.service.aio;

import org.kwater.domain.aio.AiProcessInitDTO;
import org.kwater.persistence.mybatis.aio.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {
    @Autowired
    private DashboardRepository aiReceivingInit;
//    public DatabaseService(AiReceivingInitRepository aiReceivingInit) {
//    }

    public AiProcessInitDTO getAiReceivingInit(String item)
    {
        return aiReceivingInit.getAiReceivingInit(item);
    }
}
