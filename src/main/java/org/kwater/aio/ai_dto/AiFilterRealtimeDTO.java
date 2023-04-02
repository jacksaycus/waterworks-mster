package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_filter_realtime
// 여과 공정 AI 결과 테이블
public class AiFilterRealtimeDTO
{
    private Date update_time;
    private Integer f_operation_mode;           // 운전 모드 0:수동, 1:반자동, 2:완전자동
    private Integer f_peak_mode;                // 피크 관리 모드 0:OFF, 1:ON
    private Integer f_bw_mode;                  // 역세 모드 0:OFF, 1:ON
    private String d_in_fr;                     // 혼화지 유입 유량
    private Float f_out_fr;                     // 여과지 유출 유량
    private Float f_sp;                         // 여과 속도
    private Integer f_operation_count;          // 운영지 수
    private String f_location_state;            // 지별 여과 상태
    private String f_location_le;               // 지별 수위
    private String f_location_tb;               // 지별 탁도
    private String f_location_ti;               // 지별 여과 지속시간
    private String ai_f_location_le;            // AI 지별 여과 수위 예측
    private String ai_f_location_ti;            // AI 지별 여과 지속시간 예측
    private String ai_f_location_bw_start_ti;   // AI 지별 역세 시작 시간 예측
    private String f_location_bw_wait_ti;       // 역세 후 대기 시간
    private String e_tb_b;                      // 침전지 후 탁도
    private String ai_f_operation_count;        // AI 운영지 수 예측
    private String ai_f_location_operation;     // AI 지별 여과 스케쥴
    private String pred_schedule_final;         // AI 여과 운영 스케쥴 표
}
