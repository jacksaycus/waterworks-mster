package org.kwater.aio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// Front-end 팝업을 통한 제어 명령을 저장하기 위한 class
public class InterfaceAlarmControlDTO
{
    @JsonProperty("alarm_id")
    private int alarm_id;

    @JsonProperty("alarm_time")
    private Date alarm_time;
}
