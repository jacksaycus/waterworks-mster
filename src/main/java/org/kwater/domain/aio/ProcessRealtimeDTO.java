package org.kwater.domain.aio;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// receiving_realtime, coagulant_realtime, mixing_realtime, sedimentation_realtime, filter_realtime, gac_realtime
// disinfection_realtime, ozone_realtime
public class ProcessRealtimeDTO
{
    private String name;        // 태그명
    private String value;       // 값
    private Date update_time;   // 업데이트 시간
    private Integer quality;    // 퀄리티
}
