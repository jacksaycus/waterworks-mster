package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// class_info
public class ClassInfoDTO
{
    private Integer class_index;
    private String description;     // 원수 분류 설명
    private String tb;              // 탁도
    private String ph;              // pH
    private String te;              // 수온
    private String cu;              // 전기전도도
}
