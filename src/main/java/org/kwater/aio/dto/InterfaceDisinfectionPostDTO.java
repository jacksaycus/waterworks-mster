package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 소독 후염소 알고리즘 설정값을 저장하기 위한 class
public class InterfaceDisinfectionPostDTO
{
    private float g_post_set_max;               // 후염소 주입률 최대값
    private float g_post_set_min;               // 후염소 주입률 최소값
    private float g_post_chg_limit_for_onetime; // 후염소 1회 변경 한계치
    private float h_target_cl;                  // 정수지 목표 잔류염소
}
