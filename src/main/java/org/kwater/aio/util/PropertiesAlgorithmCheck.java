package org.kwater.aio.util;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix="properties")
@ToString
@Component
public class PropertiesAlgorithmCheck
{
//    @Value("${properties.algorithm-check.period}")
//    int period;

//    @Value("${properties.algorithm-health}")
    private List<String> algorithmHealth;
}
