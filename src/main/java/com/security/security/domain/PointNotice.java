package com.security.security.domain;

import com.security.security.domain.request.MemberSignupRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter @Setter
@NoArgsConstructor
@Entity
public class PointNotice {

    @Id
    @GeneratedValue
    @Column(name = "pointnoti_id")
    private Long id;
    private String admin_id;
    private String subject;
    private String description;
}
