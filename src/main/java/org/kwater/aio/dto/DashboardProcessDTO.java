package org.kwater.aio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DashboardProcessDTO
{
    @JsonProperty("id")
    private String id;

    @JsonProperty("src")
    private String src;

    @JsonProperty("ext")
    private String ext;

    @JsonProperty("path")
    private String path;

    @JsonProperty("x")
    private double x;

    @JsonProperty("y")
    private double y;

    @JsonProperty("width")
    private float width;

    @JsonProperty("height")
    private float height;

    @JsonProperty("scale")
    private int scale;

    @JsonProperty("title")
    private String title;

    @JsonProperty("line")
    private String line;
}
