package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// ai_receiving_init, ai_coagulant_init, ai_mixing_init, ai_sedimentation_init, ai_filter_init, ai_gac_init
// ai_disinfection_init, ai_ozone_init
// 공정 별 알고리즘 내부 저장 값
public class AiProcessInitDTO
{
    private String item;    // 항목명
    private String name;    // 태그명
    private Float value;    // 값
}
