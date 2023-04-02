package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// user
public class UserDTO
{
    private String userid;      // 사용자 ID
    private String password;    // 사용자 비밀번호
    private String name;        // 사용자 이름
    private String partname;    // 부서명
    private Integer authority;  // 권한
}
