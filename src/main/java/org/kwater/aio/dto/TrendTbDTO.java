package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class TrendTbDTO
{
    private Date update_time;
    private float hs_e1_tb;
    private float hs_e2_tb;
    private float hs_f_tb;
}
