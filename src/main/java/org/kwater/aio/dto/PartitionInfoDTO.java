package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// partition_info
public class PartitionInfoDTO
{
    private String hostname;    // 대상 호스트명
    private String name;        // 파티션 명
    private long total_size;    // 전체 크기
    private long usable_size;   // 사용가능 크기
}
