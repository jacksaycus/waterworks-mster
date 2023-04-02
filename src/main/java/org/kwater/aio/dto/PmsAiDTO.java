package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// PMS AI 예측 값
public class PmsAiDTO
{
    private String moter_id;        // 모터 ID
    private Date acq_date;          // 업데이트 시간
    private float motor_de_amp;     // 모터 de amp
    private float motor_nde_amp;    // 모터 nde amp
    private float pump_de_amp;      // 펌프 de amp
    private float pump_nde_amp;     // 펌프 nde amp
}
