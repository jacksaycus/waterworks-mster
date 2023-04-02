package org.kwater.aio.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
// access_token
public class AccessTokenDTO
{
    private int token_index;
    private String access_token;    // access token
    private String userid;          // 사용자 아이디
    private String name;            // 사용자 이름
    private Integer authority;      // 사용자 권한
    private Date expiration;        // 토큰 만료 시간
}
