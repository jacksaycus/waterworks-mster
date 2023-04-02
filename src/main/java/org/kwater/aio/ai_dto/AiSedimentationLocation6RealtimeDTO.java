package org.kwater.aio.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 6지 세부현황
public class AiSedimentationLocation6RealtimeDTO
{
    // 대차 위치
    @JsonProperty("SCI-2609")
    private AiSedimentationSludgeCollectorPosition SCI_2609;

    // 슬러지 양
    @JsonProperty("AIE-5006")
    private Float AIE_5006;

    // 대차 스케쥴
    @JsonProperty("AIE-6006")
    private AiSedimentationSludgeCollectorSchedule AIE_6006;

    // 인발밸브 1 열림
    @JsonProperty("VVB-2613")
    private Integer VVB_2613;

    // 인발밸브 2 열림
    @JsonProperty("VVB-2616")
    private Integer VVB_2616;

    // 인발밸브 3 열림
    @JsonProperty("VVB-2619")
    private Integer VVB_2619;

    // 인발밸브 4 열림
    @JsonProperty("VVB-2622")
    private Integer VVB_2622;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5106")
    private Object AIE_5106;

    // 대차 시작 제어
    @JsonProperty("AIE-7006")
    private Integer AIE_7006;

    // 시작 시 제어
    @JsonProperty("AIE-8006")
    private Integer AIE_8006;

    // 시작 분 제어
    @JsonProperty("AIE-8106")
    private Integer AIE_8106;

    // 운영 상태
    @JsonProperty("operation")
    private Integer operation;
}
