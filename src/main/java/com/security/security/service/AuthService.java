package com.security.security.service;

import com.security.security.domain.Member;
import com.security.security.domain.request.MemberSignupRequestDto;
import com.security.security.repository.AuthRepository;
import com.security.security.utils.AesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

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

        if(posibleJoinMember(request.getName(), request.getRegNo()))
            throw new Exception("가입 할수 없는 회원입니다. 이름과 주민번호를 다시 확인해 주세요");

        Member member = new Member(request);
        member.encryptPassword(passwordEncoder);
        member.setRegNo(aesService.encrypt(request.getRegNo()));
        authRepository.save(member);
        return member;
    }

    /**
     * 가입 가능한 이름&주민번호 체크
     */
    private Boolean posibleJoinMember(String name, String regNo) {
        // Pair 리스트 초기화
        List<Pair<String, String>> pairList = Arrays.asList(
                Pair.of("홍길동", "900101-1111111"),
                Pair.of("김둘리", "921108-1582816"),
                Pair.of("마징가", "880601-2455116"),
                Pair.of("배지터", "910411-1656116"),
                Pair.of("손오공", "820326-2715702")
        );

        // 스트림을 사용하여 이름과 등록번호가 일치하는지 검사
        return pairList.stream()
                .anyMatch(pair -> pair.getFirst().equals(name) && pair.getSecond().equals(regNo));
    }
}
