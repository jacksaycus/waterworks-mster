package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_disinfection_realtime
// 소독 공정 AI 결과 테이블
public class AiDisinfectionRealtimeDTO
{
    private Date update_time;
    private Integer g_pre_operation_mode;   // 전염소 운전 모드 0:수동, 1:반자동, 2:완전자동
    private Integer g_peri_operation_mode;  // 중염소 운전 모드 0:수동, 1:반자동, 2:완전자동
    private Integer g_post_operation_mode;  // 후염소 운전 모드 0:수동, 1:반자동, 2:완전자동
    private Float ai_g_pre1_evaporation;    // AI 전염소 1계열 증발량 예측
    private Float ai_g_pre2_evaporation;    // AI 전염소 2계열 증발량 예측
    private Float d1_target_cl;             // 1계열 혼화지 목표 잔류염소
    private Float d2_target_cl;             // 2계열 혼화지 목표 잔류염소
    private Float e1_target_cl;             // 1계열 침전지 목표 잔류염소
    private Float e2_target_cl;             // 2계열 침전지 목표 잔류염소
    private Float h_target_cl;              // 정수지 목표 잔류염소
    private Float g_pre1_chlorination;      // 전염소 1계열 현재 주입률
    private Float g_pre2_chlorination;      // 전염소 2계열 현재 주입률
    private Float g_peri1_chlorination;     // 중염소 1계열 현재 주입률
    private Float g_peri2_chlorination;     // 중염소 2계열 현재 주입률
    private Float g_post_chlorination;      // 후염소 현재 주입률
    private Float ai_g_pre1_chlorination;   // AI 전염소 1계열 주입률 예측
    private Float ai_g_pre2_chlorination;   // AI 전염소 2계열 주입률 예측
    private Float ai_g_peri1_chlorination;  // AI 중염소 1계열 주입률 예측
    private Float ai_g_peri2_chlorination;  // AI 중염소 2계열 주입률 예측
    private Float e1_cl;                    // 1계열 침전지 잔류염소
    private Float e2_cl;                    // 2계열 침전지 잔류염소
    private Float d1_cl;                    // 1계열 혼화지 잔류염소
    private Float d2_cl;                    // 2계열 혼화지 잔류염소
    private Float d1_in_fr;                 // 1계열 혼화지 유입 유량
    private Float d2_in_fr;                 // 2계열 혼화지 유입 유량
    private Float g_pre1_corrected;         // 전염소 1계열 보정상수
    private Float g_pre2_corrected;         // 전염소 2계열 보정상수
    private Float ai_g_pre1_corrected;      // AI 전염소 1계열 보정값 예측
    private Float ai_g_pre2_corrected;      // AI 전염소 2계열 보정값 예측
    private Float ai_g_peri1_corrected;     // AI 중염소 1계열 보정값 예측
    private Float ai_g_peri2_corrected;     // AI 중염소 2계열 보정값 예측
    private Float ai_g_post_corrected;      // AI 후염소 보정값 예측
    private Float h_in_cl;                  // 정수지 유입 잔류염소
    private Float h_out_cl;                 // 정수지 유출 잔류염소
    private Float h_ph;                     // 정수지 pH
    private Float h_tb;                     // 정수지 탁도
    private Float ai_g_post_chlorination;   // AI 후염소 주입률 예측
}
