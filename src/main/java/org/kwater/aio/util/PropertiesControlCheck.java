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
public class PropertiesControlCheck
{
    @Value("${properties.control-check.period}")
    int period;
}
