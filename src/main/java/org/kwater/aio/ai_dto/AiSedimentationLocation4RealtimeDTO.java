package org.kwater.aio.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 4지 세부현황
public class AiSedimentationLocation4RealtimeDTO
{
    // 대차 위치
    @JsonProperty("SCI-2409")
    private AiSedimentationSludgeCollectorPosition SCI_2409;

    // 슬러지 양
    @JsonProperty("AIE-5004")
    private Float AIE_5004;

    // 대차 스케쥴
    @JsonProperty("AIE-6004")
    private AiSedimentationSludgeCollectorSchedule AIE_6004;

    // 인발밸브 1 열림
    @JsonProperty("VVB-2413")
    private Integer VVB_2413;

    // 인발밸브 2 열림
    @JsonProperty("VVB-2416")
    private Integer VVB_2416;

    // 인발밸브 3 열림
    @JsonProperty("VVB-2419")
    private Integer VVB_2419;

    // 인발밸브 4 열림
    @JsonProperty("VVB-2422")
    private Integer VVB_2422;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5104")
    private Object AIE_5104;

    // 대차 시작 제어
    @JsonProperty("AIE-7004")
    private Integer AIE_7004;

    // 시작 시 제어
    @JsonProperty("AIE-8004")
    private Integer AIE_8004;

    // 시작 분 제어
    @JsonProperty("AIE-8104")
    private Integer AIE_8104;

    // 운영 상태
    @JsonProperty("operation")
    private Integer operation;
}
