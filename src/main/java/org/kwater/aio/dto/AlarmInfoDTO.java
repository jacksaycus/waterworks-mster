package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// alarm_info
public class AlarmInfoDTO
{
    private int alarm_info_index;
    private int alarm_id;           // 알람 ID
    private String code_name;       // 알람 코드명
    private String display_name;    // 알람 표시명
    private String url;             // 알고리즘 링크
//    private Integer severity; // 알람 중요도 삭제
    private Integer type;           // 알람 종류 0:OFF 알람, 1:Threshold 알람
    private Integer compare;        // 알람 비교 인자 0:==, 1:<(미만), 2:<=(이하), 3:>(초과), 4:>=(이상)
    private String value;           // 알람 임계 값
    private boolean scada_send;     // SCADA 전송 여부
    private String tag;             // 태그명
}
