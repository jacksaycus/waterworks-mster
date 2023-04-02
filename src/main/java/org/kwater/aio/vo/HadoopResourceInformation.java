package org.kwater.aio.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HadoopResourceInformation
{
    @JsonProperty("maximumAllocation")
    private long maximumAllocation;

    @JsonProperty("minimumAllocation")
    private long minimumAllocation;

    @JsonProperty("name")
    private String name;

    @JsonProperty("resourceType")
    private String resourceType;

    @JsonProperty("units")
    private String units;

    @JsonProperty("value")
    private long value;
}
