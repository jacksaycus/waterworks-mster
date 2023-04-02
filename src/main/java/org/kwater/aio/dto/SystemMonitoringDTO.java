package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// system_monitoring
public class SystemMonitoringDTO
{
    private int monitoring_index;
    private String hostname;        // 호스트 명
    private Integer type;           // 모니터링 종류(1:Processor used, 2:Memory used, 3:Partition used, 4:Network Sent Throughput, 5:Network Recv Throughput, 11:분석 시스템 DB, 12:시각화 서비스 API, 13:수집 시스템 Agent, 21:AI)
    private String name;            // 모니터링 대상
    private String value;           // 모니터링 값
    private Date update_time;       // 업데이트 시간
}
