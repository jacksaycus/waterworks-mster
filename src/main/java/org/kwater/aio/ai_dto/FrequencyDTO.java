package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// 정규분포 데이터를 저장하기 위한 class
public class FrequencyDTO
{
    private String value;
    private int count;
}
