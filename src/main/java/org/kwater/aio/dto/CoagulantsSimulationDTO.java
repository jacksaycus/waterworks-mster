package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class CoagulantsSimulationDTO
{
    private int simulation_index;
    private Date reg_time;
    private Date complete_time;
    private String userid;
    private Integer state;
    private Integer cluster_id;             // null exception occurred, int -> Integer
    private String chemical1;
    private Float injection1_percent;       // null exception occurred, float -> Float
    private String chemical2;
    private Float injection2_percent;       // null exception occurred, float -> Float
    private float tb;
    private float ph;
    private float te;
    private float cu;
}
