package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 약품 시뮬레이션 데이터를 저장하기 위한 class
public class InterfaceAiCoagulantSimulationDTO
{
    private float b_tb;     // 원수 탁도
    private float b_ph;     // 원수 pH
    private float b_te;     // 원수 수온
    private float b_cu;     // 원수 전기전도도
    private float b_in_fr;  // 원수 유입 유량
    private float e1_tb;    // 1계열 침전지 탁도
    private float e2_tb;    // 2계열 침전지 탁도
}
