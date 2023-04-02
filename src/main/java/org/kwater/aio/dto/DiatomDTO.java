package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class DiatomDTO
{
    private int diatom_index;
    private Date update_time;
    private Date measure_time;
    private int pd1_diatom;
    private int pd2_diatom;
    private int pd3_diatom;
    private Integer pd1_diatom_big;
    private Integer pd2_diatom_big;
    private Integer pd3_diatom_big;
    private String pd_diatom_avg;
}
