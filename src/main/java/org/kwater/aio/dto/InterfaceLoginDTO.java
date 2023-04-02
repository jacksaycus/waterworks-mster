package org.kwater.aio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Front-end 로그인 정보를 저장하기 위한 class
public class InterfaceLoginDTO
{
    @JsonProperty("userid")
    private String userid;

    @JsonProperty("password")
    private String password;
}
