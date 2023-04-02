package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 지별 AI ON/OFF 명령을 저장하기 위한 class
public class InterfaceAiOnOffDTO
{
    private int ai; // 0:OFF, 1:ON
}
