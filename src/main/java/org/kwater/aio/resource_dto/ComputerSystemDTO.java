package org.kwater.aio.resource_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 시스템 정보 parsing을 위한 class
public class ComputerSystemDTO
{
    private String manufacturer;            // 제조사
    private String model;                   // 모델명
    private String serial;                  // 시리얼 번호
    private String firmwareManufacturer;    // 펌웨어 제조사
    private String firmwareName;            // 펌웨어 명
    private String firmwareDescription;     // 펌웨어 설명
    private String firmwareVersion;         // 펌웨어 버전
    private String firmwareReleaseDate;     // 펌웨어 릴리즈 날짜
    private String boardManufacturer;       // 보드 제조사
    private String boardModel;              // 보드 모델명
    private String boardVersion;            // 보드 버전
    private String boardSerial;             // 보드 시리얼 번호
}
