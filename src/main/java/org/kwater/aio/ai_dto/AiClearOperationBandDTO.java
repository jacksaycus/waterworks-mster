package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// ai_clear_operation_band, ai_clear_ems_operation_band, ai_clear_wide_operation_band
// 정수지 수위 밴드
public class AiClearOperationBandDTO
{
    private Date index;     // 시간
    private Double up;      // up band
    private Double down;    // down band
}
