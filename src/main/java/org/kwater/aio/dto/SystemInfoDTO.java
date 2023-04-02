package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// system_info
public class SystemInfoDTO
{
    private String hostname;        // 호스트 명
    private String name;            // 시스템 명
    private String os;              // 운영체제
    private String model;           // 모델명
    private String processor_name;  // 프로세스 명
    private Integer package_count;  // CPU 개수
    private Integer core_count;     // 코어 수
    private Integer logical_count;  // 논리 코어 수
    private long max_frequency;     // CPU 최대 주파수
    private long total_memory;      // 총 메모리
    private long available_memory;  // 사용가능 메모리
    private long db_used;           // DB 사용량
    private long db_free;           // DB 잔여량
    private Date update_time;       // 업데이트 시간
}
