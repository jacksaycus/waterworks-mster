package org.kwater.aio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class DashboardInfoDTO
{
    @JsonProperty("create_time")
    private Date create_time;

    @JsonProperty("update_time")
    private Date update_time;

    @JsonProperty("title")
    private String title;

    @JsonProperty("thumb")
    private String thumb;

    @JsonProperty("process_list")
    private List<DashboardProcessDTO> process_list;

    @JsonProperty("path_list")
    private List<DashboardPathDTO> path_list;
}
