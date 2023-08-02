package com.security.security.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberLoginRequestDto {
    private String userId;
    private String password;
}
