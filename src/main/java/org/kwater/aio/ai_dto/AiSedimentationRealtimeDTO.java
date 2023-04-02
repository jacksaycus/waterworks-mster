package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
//ai_sedimentation_realtime
// 침전 공정 AI 결과 테이블
public class AiSedimentationRealtimeDTO
{
    private Date update_time;
    private Integer AIE_1000;   // AI 운전 모드
    private Float FRI_2000;     // 원수 유입 유량
    private Float TBI_2000;     // 원수 탁도
    private Float FRI_2053;     // 1계열 APAC 혼화기 유량계값
    private Float FRI_2055;     // 1계열 Polymax 혼화기 유량계 값
    private Float FRI_2054;     // 2계열 APAC 혼화기 유량계값
    private Float FRI_2056;     // 2계열 Polymax 혼화기 유량계 값
    private Float AIE_9903;     // 1계열 침전수 전탁도
    private Float AIE_9904;     // 2계열 침전수 전탁도
    private Float TBI_2001;     // 1계열 침전수 탁도
    private Float TBI_2002;     // 2계열 침전수 탁도
    private Float AIE_9901;     // 1계열 계면계 수위
    private Float AIE_9902;     // 2계열 계면계 수위
    private Float AIE_5301;     // 1계열 AI 슬러지 발생량 예측
    private Float AIE_5302;     // 2계열 AI 슬러지 발생량 예측
    private String AIE_5200;    // 침전지 총 슬러지 발생량 트렌드
    private String AIE_9002;    // 2지 세부 현황
    private String AIE_9003;    // 3지 세부 현황
    private String AIE_9004;    // 4지 세부 현황
    private String AIE_9005;    // 5지 세부 현황
    private String AIE_9006;    // 6지 세부 현황
    private String AIE_9007;    // 7지 세부 현황
    private String AIE_9008;    // 8지 세부 현황
    private String AIE_9009;    // 9지 세부 현황
}
