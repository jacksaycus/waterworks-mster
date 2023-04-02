package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 소독 전염소 알고리즘 설정값을 저장하기 위한 class
public class InterfaceDisinfectionPreDTO
{
    private float g_pre1_set_max;               // 전염소 1계열 주입률 최대값
    private float g_pre1_set_min;               // 전염소 1계열 주입률 최소값
    private float g_pre1_chg_limit_for_onetime; // 전염소 1계열 1회 변경 한계치
    private float d1_target_cl;                 // 1계열 혼화지 목표 잔류염소
    private float e1_target_cl;                 // 1계열 침전지 목표 잔류염소
    private float g_pre2_set_max;               // 전염소 2계열 주입률 최대값
    private float g_pre2_set_min;               // 전염소 2계열 주입률 최소값
    private float g_pre2_chg_limit_for_onetime; // 전염소 2계열 1회 변경 한계치
    private float d2_target_cl;                 // 2계열 혼화지 목표 잔류염소
    private float e2_target_cl;                 // 2계열 침전지 목표 잔류염소
}
