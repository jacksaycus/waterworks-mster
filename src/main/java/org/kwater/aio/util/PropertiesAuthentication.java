package org.kwater.aio.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Setter
@Getter
@ToString
@Component
public class PropertiesAuthentication
{
    @Value("${properties.authentication.expiration}")
    long expiration;

    @Value("${properties.authentication.key}")
    String key;

    @Value("${properties.authentication.resource-monitor-token}")
    String resourceMonitorToken;

    @Value("${properties.authentication.iwater-token}")
    String iWaterToken;

    @Value("${properties.authentication.internal-token}")
    String internalToken;
}
