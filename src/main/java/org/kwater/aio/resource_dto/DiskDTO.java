package org.kwater.aio.resource_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// resource monitoring의 disk 정보를 parsing 하기 위한 class
public class DiskDTO
{
    private String model;   // 모델명
    private long size;      // 크기
}
