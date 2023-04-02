package org.kwater.aio.resource_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// resource monitoring의 프로세서 정보를 parsing하기 위한 class
public class ProcessorDTO
{
    private String name;        // 프로세서 명
    private int packageCount;   // 프로세서 물리 개수
    private int physicalCount;  // core 수
    private int logicalCount;   // 논리 core 수
    private long maxFrequency;  // 최대 주파수
    private float used;         // 사용률
}
