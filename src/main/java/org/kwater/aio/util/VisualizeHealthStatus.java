package org.kwater.aio.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@Getter
@Setter
@ToString
public class VisualizeHealthStatus
{
    private int status;

    @PostConstruct
    public void init()
    {
        status = CommonValue.HEALTH_CHECK_NORMAL;
    }

    public void setFailStatus()
    {
        if(status == CommonValue.HEALTH_CHECK_NORMAL)
        {
            status = CommonValue.HEALTH_CHECK_FAIL;
        }
        else if(status == CommonValue.HEALTH_CHECK_FAIL)
        {
            status = CommonValue.HEALTH_CHECK_ALARM;
        }
    }
}
