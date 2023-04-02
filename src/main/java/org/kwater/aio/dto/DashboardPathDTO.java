package org.kwater.aio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class DashboardPathDTO
{
    @JsonProperty("id")
    private String id;

    @JsonProperty("color")
    private String color;

    @JsonProperty("point_list")
    private List<DashboardPointDTO> point_list;

    @JsonProperty("path_point")
    private List<DashboardPointDTO> path_point;
}
