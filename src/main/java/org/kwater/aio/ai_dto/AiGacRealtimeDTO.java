package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_gac_realtime
// GAC 여과 공정 AI 결과 테이블
public class AiGacRealtimeDTO
{
    private Date update_time;
    private Integer i_operation_count;          // 운영지 수
    private Integer i_operation_mode;           // 운전 모드 0:수동, 1:반자동, 2:완전자동
    private Integer i_peak_mode;                // 피크 관리 모드 0:OFF, 1:ON
    private Integer i_bw_mode;                  // 역세 모드 0:OFF, 1:ON
    private String d_in_fr;                     // 혼화지 유입 유량
    private Float i_in_fr;                      // 활성탄 흡착지 유입 유량
    private Float i_out_fr;                     // 환성탄 흡착지 유출 유량
    private Float i_sp;                         // 여과 속도
    private String i_location_state;            // 지별 여과 상태
    private String i_location_le;               // 지별 수위
    private String e_tb_b;                      // 침전지 후 탁도
    private Float f_tb;                         // 급속여과지 탁도
    private Float i_tb;                         // 활성탄 흡착지 탁도
    private String i_location_ti;               // 지별 여과 지속시간
    private String ai_i_location_ti;            // AI 지별 여과 지속시간 예측
    private String i_location_bw_wait_ti;       // 역세 후 대기 시간
    private String ai_i_location_bw_start_ti;   // AI 역세 시작 시간 예측
    private String ai_i_operation_count;        // AI 운영지 수 예측
    private String ai_i_location_operation;     // AI 지별 여과 스케쥴
    private String pred_schedule_mv_bw;         // AI GAC 여과 운영 스케쥴 표
}
