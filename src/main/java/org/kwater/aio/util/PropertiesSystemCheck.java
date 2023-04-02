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
public class PropertiesSystemCheck
{
    @Value("${properties.system-check.period}")
    int period;

    @Value("${properties.system-check.reference}")
    int reference;
}
