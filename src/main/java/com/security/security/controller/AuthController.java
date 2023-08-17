package com.security.security.controller;

import com.security.security.utils.JwtTokenUtil;
import com.security.security.domain.*;
import com.security.security.domain.request.MemberLoginRequestDto;
import com.security.security.domain.request.MemberSignupRequestDto;
import com.security.security.domain.response.ResponseDto;
import com.security.security.service.AuthService;
import com.security.security.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/szs")
@Slf4j
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final MemberService memberService;
    private PasswordEncoder passwordEncoder;
    private JwtTokenUtil jwtTokenProvider;

    @GetMapping("/test")
    public ResponseDto feignTest(Authentication authentication) {
        return new ResponseDto(200, "OK", authentication.getName());
    }

    /**
     * 회원 가입
     */
    @Operation(summary = "회원 가입", description = "회원 가입 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "206", description = "기타 api 오류", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto signup(@RequestBody @Valid MemberSignupRequestDto request) throws Exception {
        return new ResponseDto(200, "OK", authService.signup(request));
    }

    /**
     * 로그인
     */
    @Operation(summary = "로그인", description = "로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "205", description = "잘못된 비밀번호 입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "206", description = "기타 api 오류", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto login(@RequestBody MemberLoginRequestDto request) throws IOException {
        Member members = memberService.findOne(request.getUserId());
        ResponseDto responseDto = new ResponseDto(200, "OK", null);
        if(!passwordEncoder.matches(request.getPassword(),members.getPassword())) {
            responseDto.setCode(205);
            responseDto.setMessage("잘못된 비밀번호 입니다.");
            return null;
        }

        String token = jwtTokenProvider.createToken(members.getUserId(), members.getRegNo());
        responseDto.setResult(token);

        return responseDto;
    }
}
