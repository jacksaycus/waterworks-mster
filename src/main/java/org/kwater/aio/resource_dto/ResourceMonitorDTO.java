package org.kwater.aio.resource_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
// resource monitoring을 저장하기 위한 class
public class ResourceMonitorDTO
{
    OperatingSystemDTO operatingSystem; // 운영체계
    ComputerSystemDTO computerSystem;   // 시스템
    ProcessorDTO processor;             // 프로세서
    MemoryDTO memory;                   // 메모리
    StorageDTO storage;                 // 저장장치
    List<InterfaceDTO> interfaces;      // 네트워크 인터페이스
    DbStorageDTO dbStorage;             // Database 저장공간
    String passwd;                      // 패스워드
}
