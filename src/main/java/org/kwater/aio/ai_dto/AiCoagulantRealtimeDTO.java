package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_coagulant_realtime
// 약품 공정 AI 결과 테이블
public class AiCoagulantRealtimeDTO
{
    private Date update_time;
    private Integer c_operation_mode;   // 운전 모드 0:수동, 1:반자동, 2:완전자동
    private Float b_tb;                 // 원수 탁도
    private Float b_ph;                 // 원수 pH
    private Float b_te;                 // 원수 수온
    private Float b_cu;                 // 원수 전기전도도
    private Float b_in_fr;              // 원수 유입 유량
    private String c_cf_coagulant;      // 현재 약품 종류
    private String c_mm_fr;             // 현재 약품 주입량
    private String c_cf;                // 현재 약품 주입률
    private String d_in_fr;             // 혼화지 유입 유량
    private String e_tb_b;              // 침전지 후탁도
    private Integer ai_cluster_id;      // 클러스터 ID
    private String ai_c_coagulant;      // AI 약품 종류 예측
    private String ai_c_final;          // AI 약품 주입률 최종값
    private String ai_c_result;         // AI 약품 주입률 예측
    private String ai_c_corrected;      // AI 약품 주입률 보정값
}
