package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 침전 공정 계열별 Float 값을 저장하기 위한 class
public class JsonESeriesFloat
{
    private Float series1;
    private Float series2;
}
