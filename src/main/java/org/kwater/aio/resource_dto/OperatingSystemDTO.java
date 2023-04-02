package org.kwater.aio.resource_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// resource monitoring의 운영체계 부분을 parsing하기 위한 class
public class OperatingSystemDTO
{
    private String name;        // 운영체계 명
    private String hostName;    // 호스트 명
    private String domainName;  // 도메인 명
}
