package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_mixing_realtime
// 혼화응집 공정 AI 결과 테이블
public class AiMixingRealtimeDTO
{
    private Date update_time;
    private Integer d_operation_mode;       // 운전 모드 0:수동, 1:반자동, 2:완전자동
    private Float b_te;                     // 원수 수온
    private Float b_de;                     // 물의 밀도
    private Float b_viscosity;              // 물의 점성계수
    private Float d_rq;                     // 혼화지 용량
    private Float d_fc_lt;                  // 임펠러 직경
    private Float d_fc_lt2;                 // 임펠러 직경 - 2
    private Float d_pn;                     // Power Number
    private Float d_pn2;                    // Power Number - 2
    private String d_g_value;               // G 값
    private String d_fc_location_sp;        // 지별 응집기 속도
    private String d_fc_location_state;     // 지별 응집기 상태
    private Float ai_d_te;                  // AI 전처리 후 원수 수온
    private String ai_d_fc_location_sp;     // AI 응집기 속도 예측
    private String ai_d_fc_location_sp2;    // AI 응집기 속도 예측 - 2
}
