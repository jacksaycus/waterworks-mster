package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 오존 알고리즘 설정값을 저장하기 위한 class
public class InterfaceOzoneAiDTO
{
    private float io_set;           // 주입률 기준값
    private float io_injection_max; // 최고 주입률
    private float io_injection_min; // 최저 주입률
    private float io_target_de;     // 목표 잔류오존 농도
    private float io_og_1_min;      // 오존발생기 1구간 최소값
    private float io_og_1_max;      // 오존발생기 1구간 최대값
    private float io_og_2_min;      // 오존발생기 2구간 최소값
    private float io_og_2_max;      // 오존발생기 2구간 최대값
}
