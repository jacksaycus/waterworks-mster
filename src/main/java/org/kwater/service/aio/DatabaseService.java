package org.kwater.service.aio;

import org.kwater.domain.aio.AiProcessInitDTO;
import org.kwater.persistence.mybatis.aio.AiReceivingInitDAO;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {
    private AiReceivingInitDAO aiReceivingInit;
    public DatabaseService(AiReceivingInitDAO aiReceivingInit) {
    }

    public AiProcessInitDTO getAiReceivingInit(String item)
    {
        return aiReceivingInit.select(item);
    }
}
