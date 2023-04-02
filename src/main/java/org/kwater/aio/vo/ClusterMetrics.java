package org.kwater.aio.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClusterMetrics
{
    @JsonProperty("appsSubmitted")
    private int appsSubmitted;

    @JsonProperty("appsCompleted")
    private int appsCompleted;

    @JsonProperty("appsPending")
    private int appsPending;

    @JsonProperty("appsRunning")
    private int appsRunning;

    @JsonProperty("appsFailed")
    private int appsFailed;

    @JsonProperty("appsKilled")
    private int appsKilled;

    @JsonProperty("reservedMB")
    private long reservedMB;

    @JsonProperty("availableMB")
    private long availableMB;

    @JsonProperty("allocatedMB")
    private long allocatedMB;

    @JsonProperty("reservedVirtualCores")
    private long reservedVirtualCores;

    @JsonProperty("availableVirtualCores")
    private long availableVirtualCores;

    @JsonProperty("allocatedVirtualCores")
    private long allocatedVirtualCores;

    @JsonProperty("containersAllocated")
    private int containersAllocated;

    @JsonProperty("containersReserved")
    private int containersReserved;

    @JsonProperty("containersPending")
    private int containersPending;

    @JsonProperty("totalMB")
    private long totalMB;

    @JsonProperty("totalVirtualCores")
    private long totalVirtualCores;

    @JsonProperty("totalNodes")
    private int totalNodes;

    @JsonProperty("lostNodes")
    private int lostNodes;

    @JsonProperty("unhealthyNodes")
    private int unhealthyNodes;

    @JsonProperty("decommissioningNodes")
    private int decommissioningNodes;

    @JsonProperty("decommissionedNodes")
    private int decommissionedNodes;

    @JsonProperty("rebootedNodes")
    private int rebootedNodes;

    @JsonProperty("activeNodes")
    private int activeNodes;

    @JsonProperty("shutdownNodes")
    private int shutdownNodes;

    @JsonProperty("totalUsedResourcesAcrossPartition")
    private HadoopResourcesAcrossPartition totalUsedResourcesAcrossPartition;

    @JsonProperty("totalClusterResourcesAcrossPartition")
    private HadoopResourcesAcrossPartition totalClusterResourcesAcrossPartition;
}
