package org.kwater.aio.resource_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
// resource monitoring 저장장치 정보를 parsing하기 위한 class
public class StorageDTO
{
    private List<DiskDTO> diskList;             // 디스크
    private List<PartitionDTO> partitionList;   // 파티션
}
