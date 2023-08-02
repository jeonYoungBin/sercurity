package com.security.security.service;

import com.security.security.domain.Member;
import com.security.security.domain.request.MemberSignupRequestDto;
import com.security.security.proxy.ExchangeOpenFeign;
import com.security.security.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final AesService aesService;

    /**
     * 회원 가입
     */
    public Member signup(MemberSignupRequestDto request) throws Exception {
        if(!authRepository.existById(request.getUserId()))
            throw new Exception("중복된 아이디 입니다." + "(" + request.getUserId() + ")");

        Boolean joinCheck = authRepository.posibleJoinMember(request.getName(), request.getRegNo());
        if(!joinCheck)
            throw new Exception("가입 할수 없는 회원입니다. 이름과 주민번호를 다시 확인해 주세요");

        Member member = new Member(request);
        member.encryptPassword(passwordEncoder);
        member.setRegNo(aesService.encrypt(request.getRegNo()));
        authRepository.save(member);
        return member;
    }
}
