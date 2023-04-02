package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end AI 운영 모드를 저장하기 위한 class
public class InterfaceOperationModeDTO
{
    private int operation;  // 0:수동, 1:반자동, 2:완전자동
}
