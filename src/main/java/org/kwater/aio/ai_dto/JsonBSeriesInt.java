package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 원수/착수 공정 계열별 Integer 값을 저장하기 위한 class
public class JsonBSeriesInt
{
    private Integer series1;
    private Integer series2;
}
