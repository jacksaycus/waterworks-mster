package org.kwater.aio.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 9지 세부현황
public class AiSedimentationLocation9RealtimeDTO
{
    // 대차 위치
    @JsonProperty("SCI-2909")
    private AiSedimentationSludgeCollectorPosition SCI_2909;

    // 슬러지 양
    @JsonProperty("AIE-5009")
    private Float AIE_5009;

    // 대차 스케쥴
    @JsonProperty("AIE-6009")
    private AiSedimentationSludgeCollectorSchedule AIE_6009;

    // 인발밸브 1 열림
    @JsonProperty("VVB-2913")
    private Integer VVB_2913;

    // 인발밸브 2 열림
    @JsonProperty("VVB-2916")
    private Integer VVB_2916;

    // 인발밸브 3 열림
    @JsonProperty("VVB-2919")
    private Integer VVB_2919;

    // 인발밸브 4 열림
    @JsonProperty("VVB-2922")
    private Integer VVB_2922;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5109")
    private Object AIE_5109;

    // 대차 시작 제어
    @JsonProperty("AIE-7009")
    private Integer AIE_7009;

    // 시작 시 제어
    @JsonProperty("AIE-8009")
    private Integer AIE_8009;

    // 시작 분 제어
    @JsonProperty("AIE-8109")
    private Integer AIE_8109;

    // 운영 상태
    @JsonProperty("operation")
    private Integer operation;
}
