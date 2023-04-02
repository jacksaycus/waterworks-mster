package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// tag_manage에서 운영지 범위를 구하기 위한 class
public class TagManageRangeDTO
{
    private Integer min;
    private Integer max;
}
