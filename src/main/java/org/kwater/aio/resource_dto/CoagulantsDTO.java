package org.kwater.aio.resource_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class CoagulantsDTO
{
    private int coagulants_index;
    private Date update_time;
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
