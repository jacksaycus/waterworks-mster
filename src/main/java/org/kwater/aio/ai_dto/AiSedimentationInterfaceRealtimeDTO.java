package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class AiSedimentationInterfaceRealtimeDTO
{
    private Date update_time;
    private Float AIE_9901;     // 1계열 계면계 수위
    private Float AIE_9902;     // 2계열 계면계 수위
}
