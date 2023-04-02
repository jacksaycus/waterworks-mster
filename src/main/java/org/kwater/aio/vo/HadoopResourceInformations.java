package org.kwater.aio.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class HadoopResourceInformations
{
    @JsonProperty("resourceInformation")
    List<HadoopResourceInformation> resourceInformation;
}
