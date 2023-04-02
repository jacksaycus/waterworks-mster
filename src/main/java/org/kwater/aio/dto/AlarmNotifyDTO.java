package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// alarm_notify
public class AlarmNotifyDTO
{
    private int alarm_notify_index;
    private int alarm_id;           // 알람 ID
    private Date alarm_time;        // 알람 시간
    private String hostname;        // 알람 대상 호스트 명
    private String value;           // 알람 값
//    private boolean ack_state;    // 알람 네비게이터 삭제
}
