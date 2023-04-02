package org.kwater.aio.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HadoopResourcesAcrossPartition
{
    @JsonProperty("memory")
    private long memory;

    @JsonProperty("vCores")
    private int vCores;

    @JsonProperty("resourceInformations")
    private HadoopResourceInformations resourceInformations;
}
