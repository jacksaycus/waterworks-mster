package org.kwater.aio.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class NameNodeJMX
{
    @JsonProperty("name")
    private String name;

    @JsonProperty("modelerType")
    private String modelerType;

    @JsonProperty("tag.Context")
    private String tagContext;

    @JsonProperty("tag.EnabledEcPolicies")
    private String tagEnabledEcPolicies;

    @JsonProperty("tag.HAState")
    private String tagHaSate;

    @JsonProperty("tag.TotalSyncTimes")
    private String tagTotalSyncTimes;

    @JsonProperty("tag.Hostname")
    private String tagHostname;

    @JsonProperty("BlocksTotal")
    private long blocksTotal;

    @JsonProperty("MissingBlocks")
    private long missingBlocks;

    @JsonProperty("MissingReplOneBlocks")
    private long missingReplOneBlocks;

    @JsonProperty("ExpiredHeartbeats")
    private long expiredHeartbeat;

    @JsonProperty("TransactionsSinceLastCheckpoint")
    private long transactionsSinceLastCheckpoint;

    @JsonProperty("TransactionsSinceLastLogRoll")
    private long transactionsSinceLastLogRoll;

    @JsonProperty("LastWrittenTransactionId")
    private long lastWrittenTransactionId;

    @JsonProperty("LastCheckpointTime")
    private Date lastCheckpointTime;

    @JsonProperty("LowRedundancyBlocks")
    private long lowRedundancyBlocks;

    @JsonProperty("CorruptBlocks")
    private long corruptBlocks;

    @JsonProperty("LowRedundancyReplicatedBlocks")
    private long lowRedundancyReplicatedBlocks;

    @JsonProperty("CorruptReplicatedBlocks")
    private long corruptReplicatedBlocks;

    @JsonProperty("MissingReplicatedBlocks")
    private long missingReplicatedBlocks;

    @JsonProperty("MissingReplicationOneBlocks")
    private long missingReplicationOneBlocks;

    @JsonProperty("BytesInFutureReplicatedBlocks")
    private long bytesInFutureReplicatedBlocks;

    @JsonProperty("PendingDeletionReplicatedBlocks")
    private long pendingDeletionReplicatedBlocks;

    @JsonProperty("HighestPriorityLowRedundancyReplicatedBlocks")
    private long highestPriorityLowRedundancyReplicatedBlocks;

    @JsonProperty("LowRedundancyECBlockGroups")
    private long lowRedundancyECBlockGroups;

    @JsonProperty("CorruptECBlockGroups")
    private long corruptECBlockGroups;

    @JsonProperty("MissingECBlockGroups")
    private long missingECBlockGroups;

    @JsonProperty("BytesInFutureECBlockGroups")
    private long bytesInFutureECBlockGroups;

    @JsonProperty("PendingDeletionECBlocks")
    private long pendingDeletionECBlocks;

    @JsonProperty("HighestPriorityLowRedundancyECBlocks")
    private long highestPriorityLowRedundancyECBlocks;

    @JsonProperty("CapacityTotal")
    private long capacityTotal;

    @JsonProperty("CapacityTotalGB")
    private float capacityTotalGB;

    @JsonProperty("CapacityUsed")
    private long capacityUsed;

    @JsonProperty("CapacityUsedGB")
    private float CapacityUsedGB;

    @JsonProperty("CapacityRemaining")
    private long capacityRemaining;

    @JsonProperty("CapacityRemainingGB")
    private float capacityRemainingGB;

    @JsonProperty("CapacityUsedNonDFS")
    private long capacityUsedNonDFS;

    @JsonProperty("ProvidedCapacityTotal")
    private long providedCapacityTotal;

    @JsonProperty("TotalLoad")
    private long totalLoad;

    @JsonProperty("SnapshottableDirectories")
    private long snapshottableDirectories;

    @JsonProperty("Snapshots")
    private long snapshots;

    @JsonProperty("NumEncryptionZones")
    private long numEncryptionZones;

    @JsonProperty("LockQueueLength")
    private long lockQueueLength;

    @JsonProperty("NumFilesUnderConstruction")
    private long numFilesUnderConstruction;

    @JsonProperty("NumActiveClients")
    private long numActiveClients;

    @JsonProperty("FilesTotal")
    private long filesTotal;

    @JsonProperty("PendingReplicationBlocks")
    private long pendingReplicationBlocks;

    @JsonProperty("PendingReconstructionBlocks")
    private long pendingReconstructionBlocks;

    @JsonProperty("UnderReplicatedBlocks")
    private long underReplicatedBlocks;

    @JsonProperty("ScheduledReplicationBlocks")
    private long scheduledReplicationBlocks;

    @JsonProperty("PendingDeletionBlocks")
    private long pendingDeletionBlocks;

    @JsonProperty("TotalReplicatedBlocks")
    private long totalReplicatedBlocks;

    @JsonProperty("TotalECBlockGroups")
    private long totalECBlockGroups;

    @JsonProperty("ExcessBlocks")
    private long excessBlocks;

    @JsonProperty("NumTimedOutPendingReplications")
    private long numTimedOutPendingReplications;

    @JsonProperty("NumTimedOutPendingReconstructions")
    private long numTimedOutPendingReconstructions;

    @JsonProperty("PostponedMisreplicatedBlocks")
    private long postponedMisreplicatedBlocks;

    @JsonProperty("PendingDataNodeMessageCount")
    private long pendingDataNodeMessageCount;

    @JsonProperty("MillisSinceLastLoadedEdits")
    private long millisSinceLastLoadedEdits;

    @JsonProperty("BlockCapacity")
    private int blockCapacity;

    @JsonProperty("NumLiveDataNodes")
    private int numLiveDataNodes;

    @JsonProperty("NumDeadDataNodes")
    private int numDeadDataNodes;

    @JsonProperty("NumDecomLiveDataNodes")
    private int numDecomLiveDataNodes;

    @JsonProperty("NumDecomDeadDataNodes")
    private int numDecomDeadDataNodes;

    @JsonProperty("VolumeFailuresTotal")
    private int volumeFailuresTotal;

    @JsonProperty("EstimatedCapacityLostTotal")
    private long estimatedCapacityLostTotal;

    @JsonProperty("NumDecommissioningDataNodes")
    private int numDecommissioningDataNodes;

    @JsonProperty("StaleDataNodes")
    private int staleDataNodes;

    @JsonProperty("NumStaleStorages")
    private int numStaleStorages;

    @JsonProperty("TotalSyncCount")
    private long totalSyncCount;

    @JsonProperty("TotalFiles")
    private long totalFiles;

    @JsonProperty("NumInMaintenanceLiveDataNodes")
    private int numInMaintenanceLiveDataNodes;

    @JsonProperty("NumInMaintenanceDeadDataNodes")
    private int numInMaintenanceDeadDataNodes;

    @JsonProperty("NumEnteringMaintenanceDataNodes")
    private int numEnteringMaintenanceDataNodes;
}
