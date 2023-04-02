package org.kwater.aio.resource_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// resource monitoring의 network interface를 parsing하기 위한 class
public class InterfaceDTO
{
    private String name;        // 인터페이스명
    private String displayName; // 표시명
    private String ipv4;        // IP 주소
    private String mac;         // MAC 주소
    private long sentBytes;     // 송신 데이터 크기
    private long recvBytes;     // 수신 데이터 크기

}
