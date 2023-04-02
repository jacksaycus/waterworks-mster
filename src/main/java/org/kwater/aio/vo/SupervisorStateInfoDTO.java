package org.kwater.aio.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class SupervisorStateInfoDTO
{
    @JsonProperty("description")
    private String description;

    @JsonProperty("exitstatus")
    private int exitstatus;

    @JsonProperty("group")
    private String group;

    @JsonProperty("logfile")
    private String logfile;

    @JsonProperty("name")
    private String name;

    @JsonProperty("now")
    private Date now;

    @JsonProperty("pid")
    private int pid;

    @JsonProperty("spawnerr")
    private String spawnerr;

    @JsonProperty("start")
    private Date start;

    @JsonProperty("state")
    private int state;

    @JsonProperty("statename")
    private String statename;

    @JsonProperty("stderr_logfile")
    private String stderr_logfile;

    @JsonProperty("stdout_logfile")
    private String stdout_logfile;

    @JsonProperty("stop")
    private Date stop;
}
