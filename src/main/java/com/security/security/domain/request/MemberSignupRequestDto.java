package com.security.security.domain.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
public class MemberSignupRequestDto {
    @NotNull(message = "아이디를 넣어주세요")
    private String userId;
    @NotNull(message = "패스워드를 넣어주세요")
    private String password;
    @NotNull(message = "이름을 넣어주세요")
    private String name;
    @NotNull(message = "주민번호를 넣어주세요")
    private String regNo;
}
