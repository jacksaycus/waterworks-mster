package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 약품 공정 계열별 String 값을 저장하기 위한 class
public class JsonCSeriesString
{
    private String series1;
    private String series2;
}
