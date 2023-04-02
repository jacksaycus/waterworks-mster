package org.kwater.aio.resource_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// resource monitoring의 partition 정보를 parsing하기 위한 class
public class PartitionDTO
{
    private String name;        // 파티션 명
    private long totalSize;     // 전체 크기
    private long usableSize;    // 사용가능한 크기
    private float used;         // 사용한 크기
}
