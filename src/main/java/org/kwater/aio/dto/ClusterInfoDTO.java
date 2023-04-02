package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// cluster_info
public class ClusterInfoDTO
{
    private int cluster_id;
    private String water_purification;  // 정수장 코드
    private float tb_min;               // 탁도 최소값
    private float tb_avg;               // 탁도 평균값
    private float tb_max;               // 탁도 최대값
    private float ph_min;               // pH 최소값
    private float ph_avg;               // pH 평균값
    private float ph_max;               // pH 최대값
    private float te_min;               // 수온 최소값
    private float te_avg;               // 수온 평균값
    private float te_max;               // 수온 최대값
    private float cu_min;               // 전기전도도 최소값
    private float cu_avg;               // 전기전도도 평균값
    private float cu_max;               // 전기전도도 최대값
}
