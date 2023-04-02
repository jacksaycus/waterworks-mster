package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// tag_description
public class TagDescriptionDTO
{
    private int tag_index;
    private String name;        // 태그 명
    private String description; // 설명
    private String created;     // 생성일
}
