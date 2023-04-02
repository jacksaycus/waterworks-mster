package org.kwater.aio.resource_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// resource monitor의 Database storage를 parsing 하기 위한 class
public class DbStorageDTO
{
    private long used;
    private long free;
}
