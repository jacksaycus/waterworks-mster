package org.kwater.aio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DashboardPointDTO
{
    @JsonProperty("x")
    private double x;

    @JsonProperty("y")
    private double y;
}
