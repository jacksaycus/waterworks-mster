package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// system_config
public class SystemConfigDTO
{
    private String scada1_address;      // SCADA 1번 주소
    private int scada1_port;            // SCADA 1번 port
    private int daq1_port;              // 수집기 1번 port
    private String scada2_address;      // SCADA 2번 주소
    private int scada2_port;            // SCADA 2번 port
    private int daq2_port;              // 수집기 2번 port
    private String analysis1_address;   // 분석 서버 1번 주소
    private int analysis1_rm;           // 분석 서버 1번 resource manager port
    private int analysis1_nm;           // 분석 서버 1번 node manager port
    private int analysis1_nn;           // 분석 서버 1번 name node port
    private String analysis2_address;   // 분석 서버 2번 주소
    private int analysis2_rm;           // 분석 서버 2번 resource manager port
    private int analysis2_nm;           // 분석 서버 2번 node manager port
    private int analysis2_nn;           // 분석 서버 2번 name node port
    private String kafka;               // Kafka 주소
}
