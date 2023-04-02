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
public class PropertiesDelegate
{
    @Value("${properties.delegate.address}")
    String address;

    @Value("${properties.delegate.version}")
    String version;
}
