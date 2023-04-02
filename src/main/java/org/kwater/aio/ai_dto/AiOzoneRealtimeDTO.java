package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_ozone_realtime
// 오존 공정 AI 결과 테이블
public class AiOzoneRealtimeDTO
{
    private Date update_time;
    private Integer io_operation_mode;  // 운전 모드 0:수동, 1:반자동, 2:완전자동
    private Float io_injection;         // 현재 오존 주입률
    private Float io_de;                // 용존오존 농도
    private Float io_pre1_de;           // 1계열 전단 용존오존 농도
    private Float io_peri1_de;          // 1계열 중간 용존오존 농도
    private Float io_post1_de;          // 1계열 후단 용존오존 농도
    private Float io_pre2_de;           // 2계열 전단 용존오존 농도
    private Float io_peri2_de;          // 2계열 중간 용존오존 농도
    private Float io_post2_de;          // 2계열 후단 용존오존 농도
    private Float ai_io_injection;      // AI 오존 주입률 예측
    private Float io_og_qu;             // 오존 통 생산량
    private Float io_hrt1;              // HRT1
    private Float io_hrt2;              // HRT2
    private Float io_hrt3;              // HRT3
    private Float ai_io_de1;            // 1구간 k계산 오존 측정값
    private Float ai_io_de2;            // 2구간 k계산 오존 측정값
    private Float ai_io_de3;            // 3구간 k계산 오존 측정값
    private Float io_hrt_q1;            // 누적 HRT1
    private Float io_hrt_q2;            // 누적 HRT2
    private Float io_hrt_q3;            // 누적 HRT3
    private Float ai_io_ln2;            // Ln2 값
    private Float ai_io_ln3;            // Ln3 값
    private Float ai_io_k2;             // 속도상수 k2 값
    private Float ai_io_k3;             // 속도상수 k3 값
    private Float ai_io_c0;             // C0 값
    private Float ai_io_id;             // ID
    private Integer ai_io_state;        // AI 결정 운전 상태
    private Float ai_io_normal;         // AI 정상상태 결정 주입률
    private Float ai_io_abnormal1;      // AI 이상1상태 결정 주입률
    private Float ai_io_abnormal2;      // AI 이상2상태 결정 주입률
}
