package org.kwater.aio.resource_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// resource monitoring의 Memory를 parsing하기 위한 class
public class MemoryDTO
{
    private long totalMemory;       // 전체 메모리 용량
    private long availableMemory;   // 사용가능한 메모리 용량
    private float used;             // 사용한 메모리 용량
    private long swapTotal;         // 전체 SWAP 용량
    private long swapUsed;          // 사용한 SWAP 용량
    private long swapPagesIn;       // Page in SWAP 용량
    private long swapPagesOut;      // Page out SWAP 용량
}
