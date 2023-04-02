package org.kwater.aio.ai_dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 7지 세부현황
public class AiSedimentationLocation7RealtimeDTO
{
    // 대차 위치
    @JsonProperty("SCI-2709")
    private AiSedimentationSludgeCollectorPosition SCI_2709;

    // 슬러지 양
    @JsonProperty("AIE-5007")
    private Float AIE_5007;

    // 대차 스케쥴
    @JsonProperty("AIE-6007")
    private AiSedimentationSludgeCollectorSchedule AIE_6007;

    // 인발밸브 1 열림
    @JsonProperty("VVB-2713")
    private Integer VVB_2713;

    // 인발밸브 2 열림
    @JsonProperty("VVB-2716")
    private Integer VVB_2716;

    // 인발밸브 3 열림
    @JsonProperty("VVB-2719")
    private Integer VVB_2719;

    // 인발밸브 4 열림
    @JsonProperty("VVB-2722")
    private Integer VVB_2722;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5107")
    private Object AIE_5107;

    // 대차 시작 제어
    @JsonProperty("AIE-7007")
    private Integer AIE_7007;

    // 시작 시 제어
    @JsonProperty("AIE-8007")
    private Integer AIE_8007;

    // 시작 분 제어
    @JsonProperty("AIE-8107")
    private Integer AIE_8107;

    // 운영 상태
    @JsonProperty("operation")
    private Integer operation;
}
