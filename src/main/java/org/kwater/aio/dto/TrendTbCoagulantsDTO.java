package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class TrendTbCoagulantsDTO
{
    private Date update_time;
    private float hs_tb;
    private float hs_e1_tb;
    private float hs_e2_tb;
    private float ai_coagulants1_input;
    private float ai_coagulants2_input;
    private String ai_coagulants1_type;
    private String ai_coagulants2_type;
    private float real_coagulants1_input;
    private float real_coagulants2_input;
    private String real_coagulants1_type;
    private String real_coagulants2_type;
}
