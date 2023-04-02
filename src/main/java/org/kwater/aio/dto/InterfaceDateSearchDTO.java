package org.kwater.aio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// Front-end 시간 검색 값을 저장하기 위한 class
public class InterfaceDateSearchDTO
{
    @JsonProperty("start_time")
    private Date start_time;

    @JsonProperty("end_time")
    private Date end_time;
}
