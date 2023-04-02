package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// login_history
public class LoginHistoryDTO
{
    private int history_index;
    private String userid;      // 사용자 ID
    private String name;        // 사용자 이름
    private Integer type;       // 이력 종류 (-1:비정상 로그아웃, 0:로그아웃, 1:로그인)
    private Date timestamp;     // 이력 발생 시간
    private String address;     // 이력 발생 주소
}
