package com.security.security.utils;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    @Value("${jwt.secretKey}")
    private String secretKey;

    // 토큰 유효시간 30분
    private long tokenValidTime = 30 * 60 * 1000L;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    //JWT 토큰 생성
    public String createToken(String userId, String regNo) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("userId", userId);
        claims.put("regNo", regNo);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    //토큰의 유효성 + 만료일자 확인
    public boolean validationToken(String jstToken) throws JwtException {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jstToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        }
    }

    public String getMemberInfo(String jwt) throws RuntimeException {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt)
                    .getBody().get("userId", String.class);

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if(authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }

}
