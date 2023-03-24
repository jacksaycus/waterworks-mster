package org.kwater.domain.aio;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_receiving_realtime
// 착수 공정 AI 결과 테이블
public class AiReceivingRealtimeDTO
{
    private Date update_time;
    private Integer b_operation_mode;   // 운전 모드 0:수동, 1:반자동, 2:완전자동
    private Integer b_ems_mode;         // EMS 모드 0:OFF, 1:ON
    private String b_in_fr;             // 원수 유입 유량
    private Float b_in_pr;              // 원수 유입 압력
    private String b_out_fr;            // 원수 유출 유량
    private String b_vv_po;             // 원수 조절 밸브 개도
    private String h_le;                // 정수지 수위
    private Float h_out_fr;             // 정수지 유출 유량
    private String ai_h_out_fr;         // AI 정수지 유출 유량 예측
    private String ai_b_in_fr;          // AI 원수 유입 유량 예측
    private String ai_b_vv_po;          // AI 원수 조절 밸브 개도 예측
    private String ai_b_in_fr_trend;    // AI 정수 유입 유량 트렌드
}
