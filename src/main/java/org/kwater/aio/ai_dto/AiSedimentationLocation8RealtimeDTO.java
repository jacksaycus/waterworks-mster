package org.kwater.aio.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 8지 세부현황
public class AiSedimentationLocation8RealtimeDTO
{
    // 대차 위치
    @JsonProperty("SCI-2809")
    private AiSedimentationSludgeCollectorPosition SCI_2809;

    // 슬러지 양
    @JsonProperty("AIE-5008")
    private Float AIE_5008;

    // 대차 스케쥴
    @JsonProperty("AIE-6008")
    private AiSedimentationSludgeCollectorSchedule AIE_6008;

    // 인발밸브 1 열림
    @JsonProperty("VVB-2813")
    private Integer VVB_2813;

    // 인발밸브 2 열림
    @JsonProperty("VVB-2816")
    private Integer VVB_2816;

    // 인발밸브 3 열림
    @JsonProperty("VVB-2819")
    private Integer VVB_2819;

    // 인발밸브 4 열림
    @JsonProperty("VVB-2822")
    private Integer VVB_2822;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5108")
    private Object AIE_5108;

    // 대차 시작 제어
    @JsonProperty("AIE-7008")
    private Integer AIE_7008;

    // 시작 시 제어
    @JsonProperty("AIE-8008")
    private Integer AIE_8008;

    // 시작 분 제어
    @JsonProperty("AIE-8108")
    private Integer AIE_8108;

    // 운영 상태
    @JsonProperty("operation")
    private Integer operation;
}
