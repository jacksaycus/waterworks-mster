package org.kwater.aio.ai_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전지 3지 세부현황
public class AiSedimentationLocation3RealtimeDTO
{
    // 대차 위치
    @JsonProperty("SCI-2309")
    private AiSedimentationSludgeCollectorPosition SCI_2309;

    // 슬러지 양
    @JsonProperty("AIE-5003")
    private Float AIE_5003;

    // 대차 스케쥴
    @JsonProperty("AIE-6003")
    private AiSedimentationSludgeCollectorSchedule AIE_6003;

    // 인발밸브 1 열림
    @JsonProperty("VVB-2313")
    private Integer VVB_2313;

    // 인발밸브 2 열림
    @JsonProperty("VVB-2316")
    private Integer VVB_2316;

    // 인발밸브 3 열림
    @JsonProperty("VVB-2319")
    private Integer VVB_2319;

    // 인발밸브 4 열림
    @JsonProperty("VVB-2322")
    private Integer VVB_2322;

    // 슬러지 양 트렌드
    @JsonProperty("AIE-5103")
    private Object AIE_5103;

    // 대차 시작 제어
    @JsonProperty("AIE-7003")
    private Integer AIE_7003;

    // 시작 시 제어
    @JsonProperty("AIE-8003")
    private Integer AIE_8003;

    // 시작 분 제어
    @JsonProperty("AIE-8103")
    private Integer AIE_8103;

    // 운영 상태
    @JsonProperty("operation")
    private Integer operation;
}
