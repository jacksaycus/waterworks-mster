package org.kwater.aio.ai_dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// 침전지 수집기 운영 스케쥴
public class AiSedimentationSludgeCollectorSchedule
{
    private String latest;      // 최종 종료 시간
    private String next_start;  // 다음 시작 시간
    private String next_end;    // 다음 종료 시간
    private String start;       // 시작 시간
    private String stop;        // 종료 시간
    private String inbal;       // 인발 시간
    private String ai_mode;
}
