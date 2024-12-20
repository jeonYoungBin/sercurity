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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedOperation;
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
    public ResponseEntity<MemberSignupResponse> signup(@RequestBody @Valid MemberSignupRequestDto request) throws Exception {
        return ResponseEntity.ok(AuthMapper.INSTANCE.of(authService.signup(request)));
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
    public ResponseEntity<TokenResponse> login(@RequestBody MemberLoginRequestDto request) throws Exception {
        return ResponseEntity.ok(TokenResponse.builder().token(memberService.login(request.getUserId(), request.getPassword())).build());
    }

    @Getter
    @Builder
    static class TokenResponse {
        private String token;
    }

    @Getter
    @Builder
    static class MemberLoginRequest {
        private String userId;
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class MemberSignupResponse {
        private Long id;
        private String userId;
        private String name;
        private String regNo;
    }

    @Mapper
    interface AuthMapper {
        AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

        MemberSignupResponse of(Member member);
    }
}
