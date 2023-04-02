package org.kwater.aio.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 5지 세부현황
public class AiSedimentationLocation5RealtimeDTO
{
    // 대차 위치
    @JsonProperty("SCI-2509")
    private AiSedimentationSludgeCollectorPosition SCI_2509;

    // 슬러지 양
    @JsonProperty("AIE-5005")
    private Float AIE_5005;

    // 대차 스케쥴
    @JsonProperty("AIE-6005")
    private AiSedimentationSludgeCollectorSchedule AIE_6005;

    // 인발밸브 1 열림
    @JsonProperty("VVB-2513")
    private Integer VVB_2513;

    // 인발밸브 2 열림
    @JsonProperty("VVB-2516")
    private Integer VVB_2516;

    // 인발밸브 3 열림
    @JsonProperty("VVB-2519")
    private Integer VVB_2519;

    // 인발밸브 4 열림
    @JsonProperty("VVB-2522")
    private Integer VVB_2522;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5105")
    private Object AIE_5105;

    // 대차 시작 제어
    @JsonProperty("AIE-7005")
    private Integer AIE_7005;

    // 시작 시 제어
    @JsonProperty("AIE-8005")
    private Integer AIE_8005;

    // 시작 분 제어
    @JsonProperty("AIE-8105")
    private Integer AIE_8105;

    // 운영 상태
    @JsonProperty("operation")
    private Integer operation;
}
