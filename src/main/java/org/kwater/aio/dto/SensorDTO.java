package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class SensorDTO
{
    private int sensor_index;
    private Date update_time;
    private int pd1_diatom;
    private int pd2_diatom;
    private int pd3_diatom;
    private Integer pd1_diatom_big;
    private Integer pd2_diatom_big;
    private Integer pd3_diatom_big;
    private String pd_diatom_avg;
    private float pd_toc;
    private float pd_ch;
    private float sj_al;
    private float hs_tb;
    private float hs_ph;
    private float hs_te;
    private float hs_cu;
    private float hs_e1_tb;
    private float hs_e2_tb;
    private float hs_f_tb;
    private float ai_coagulants1_input;
    private float ai_coagulants2_input;
    private String ai_coagulants1_type;
    private String ai_coagulants2_type;
    private float scl_coagulants1_input;
    private float scl_coagulants2_input;
    private String scl_coagulants1_type;
    private String scl_coagulants2_type;
    private float user_coagulants1_input;
    private float user_coagulants2_input;
    private String user_coagulants1_type;
    private String user_coagulants2_type;
    private float real_coagulants1_input;
    private float real_coagulants2_input;
    private String real_coagulants1_type;
    private String real_coagulants2_type;
    private Integer operation1_mode;
    private Integer operation2_mode;
}
