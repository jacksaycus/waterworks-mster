package org.kwater.aio.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 2지 세부현황
public class AiSedimentationLocation2RealtimeDTO
{
    // 대차 위치
    @JsonProperty("SCI-2209")
    private AiSedimentationSludgeCollectorPosition SCI_2209;

    // 슬러지 양
    @JsonProperty("AIE-5002")
    private Float AIE_5002;

    // 대차 스케줄
    @JsonProperty("AIE-6002")
    private AiSedimentationSludgeCollectorSchedule AIE_6002;

    // 인발밸브 1 열림
    @JsonProperty("VVB-2213")
    private Integer VVB_2213;

    // 인발밸브 2 열림
    @JsonProperty("VVB-2216")
    private Integer VVB_2216;

    // 인발밸브 3 열림
    @JsonProperty("VVB-2219")
    private Integer VVB_2219;

    // 인발밸브 4 열림
    @JsonProperty("VVB-2222")
    private Integer VVB_2222;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5102")
    private Object AIE_5102;

    // 대차 시작 제어
    @JsonProperty("AIE-7002")
    private Integer AIE_7002;

    // 시작 시 제어
    @JsonProperty("AIE-8002")
    private Integer AIE_8002;

    // 시작 분 제어
    @JsonProperty("AIE-8102")
    private Integer AIE_8102;

    // 운영 상태
    @JsonProperty("operation")
    private Integer operation;
}
