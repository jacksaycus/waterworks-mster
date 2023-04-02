package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 혼화응집 단 별 Float 값을 저장하기 위한 class
public class JsonDStepFloat
{
    private Float step1;
    private Float step2;
    private Float step3;
}
