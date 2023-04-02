package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 침전 공정 알고리즘 설정값을 저장하기 위한 class
public class InterfaceSedimentationScDTO
{
    private float e_sc_set_sludge_q;    // 슬러지 수집기 운행 기준 적산 슬러지 양
    private float e_sc_set_max_wait;    // 슬러지 수집기 대기 최대 일
    private float e_set_lt;             // 슬러지 수집기 편도 운전 거리
    private float e_sc_set_ti;          // 슬러지 수집기 운전 시간
}
