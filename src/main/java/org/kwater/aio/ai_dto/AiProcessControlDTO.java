package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_receiving_control, ai_coagulant_control, ai_mixing_control, ai_sedimentation_control, ai_filter_control,
// ai_gac_control, ai_disinfection_control, ai_ozone_control
// 공정 별 제어 테이블
public class AiProcessControlDTO
{
    private Date update_time;       // 업데이트 시간
    private Date run_time;          // 실행 시간
    private String name;            // 태그명
    private String value;           // 제어 값
    private String compare_value;   // 비교 값
    private Integer kafka_flag;     // kafka_flag 0:Default, 1:ai_popup, 2:ai_confirm, 3:send_complete
    private Integer control_flag;   // 제어 후 상태 확인 0:Default, 1:no change, 2:Change
}
