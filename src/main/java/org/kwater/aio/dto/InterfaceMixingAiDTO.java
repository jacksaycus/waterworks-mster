package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 혼화응집 알고리즘 설정값을 저장하기 위한 class
public class InterfaceMixingAiDTO
{
    private float d_g_value_step1;  // 응집기 1단 교반강도 G 값
    private float d_g_value_step2;  // 응집기 2단 교반강도 G 값
    private float d_g_value_step3;  // 응집기 3단 교반강도 G 값
}
