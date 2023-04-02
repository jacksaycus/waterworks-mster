package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class CoagulantsAnalysisDTO
{
    private Date log_time;
    private Date start_time;
    private Date end_time;
    private Date reg_time;
    private String water_purification;
    private int cluster_id;
    private String chemical1;
    private float injection1_percent;
    private float injection1_ai;
    private float injection1_revision;
    private float injection1_amount;
    private float d1_fr;
    private String chemical2;
    private float injection2_percent;
    private float injection2_ai;
    private float injection2_revision;
    private float injection2_amount;
    private float d2_fr;
    private float tb;
    private float ph;
    private float te;
    private float cu;
    private int total_count;
    private int collect_count;
    private int available_count;
    private int error_count;
    private int missing_count;
}
