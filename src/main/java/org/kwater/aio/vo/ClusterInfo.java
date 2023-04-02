package org.kwater.aio.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class ClusterInfo
{
    @JsonProperty("id")
    private long id;

    @JsonProperty("startedOn")
    private long startedOn;

    @JsonProperty("state")
    private String state;

    @JsonProperty("haState")
    private String haState;

    @JsonProperty("rmStateStoreName")
    private String rmStateStoreName;

    @JsonProperty("resourceManagerVersion")
    private String resourceManagerVersion;

    @JsonProperty("resourceManagerBuildVersion")
    private String resourceManagerBuildVersion;

    @JsonProperty("resourceManagerVersionBuiltOn")
    private Date resourceManagerVersionBuiltOn;

    @JsonProperty("hadoopVersion")
    private String hadoopVersion;

    @JsonProperty("hadoopBuildVersion")
    private String hadoopBuildVersion;

    @JsonProperty("hadoopVersionBuiltOn")
    private Date hadoopVersionBuiltOn;

    @JsonProperty("haZooKeeperConnectionState")
    private String haZooKeeperConnectionState;
}
