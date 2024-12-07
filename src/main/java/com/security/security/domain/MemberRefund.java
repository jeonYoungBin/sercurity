package com.security.security.domain;

import com.security.security.domain.request.MemberSignupRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Builder
public class MemberRefund {
    private Long retirementAmount;
    private Long determinedTaxAmount;
    private String name;
}
