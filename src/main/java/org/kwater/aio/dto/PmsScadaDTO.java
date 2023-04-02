package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// PMS SCADA 값
public class PmsScadaDTO
{
    private String pump_scada_id;       // 펌프 ID
    private float brg_motor_de_temp;    // 모터 de 온도
    private float brg_motor_nde_temp;   // 모터 nde 온도
    private float brg_pump_de_temp;     // 펌프 de 온도
    private float brg_pump_nde_temp;    // 펌프 nde 온도
    private Date acq_date;              // 업데이트 시간
}
