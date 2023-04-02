package org.kwater.aio.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
public class PropertiesVisualizeCheck
{
    @Value("${properties.visualize-check.period}")
    int period;

    @Value("${properties.visualize-check.address}")
    String address;

    @Value("${properties.visualize-check.port}")
    int port;
}
