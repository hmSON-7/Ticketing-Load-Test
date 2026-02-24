package com.ticket.backend.config.security;

import com.ticket.backend.db.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpire;
    private final long refreshTokenExpire;

    public JwtUtil(@Value("${jwt.secret}") String key,
                   @Value("${jwt.access-token-expire-ms}") long access,
                   @Value("${jwt.refresh-token-expire-ms}") long refresh) {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpire = access;
        this.refreshTokenExpire = refresh;
    }

    // 엑세스 토큰 생성 메서드
    public String generateAccessToken(Member member) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + accessTokenExpire);

        return Jwts.builder().subject(member.getUsername())
                .claim("memberId", member.getMemberId())
                .claim("email", member.getEmail())
                .claim("role", member.getRole())
                .claim("type", "access")
                .issuedAt(now).expiration(expire)
                .signWith(secretKey).compact();
    }

    // 리프레시 토큰 생성 메서드
    public String generateRefreshToken(Member member) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + refreshTokenExpire);

        return Jwts.builder().subject(member.getUsername())
                .claim("type", "refresh")
                .issuedAt(now).expiration(expire)
                .signWith(secretKey).compact();
    }

    public Claims parseClaimsOrThrow(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload();
    }

    // 토큰 파싱 메서드
    public Claims parseClaimsAllowExpired(String token) {
        try {
            return parseClaimsOrThrow(token);
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 토큰 만료 검증 메소드
    // 만료되었을 경우 true 반환
    public boolean isTokenExpired(String token) {
        Date expiration = parseClaimsOrThrow(token).getExpiration();
        return expiration.before(new Date());
    }

    // 토큰 전체 유효성 검사(서명 + 구조 + 만료) 메서드
    public boolean isTokenValid(String token, String expectedType) {
        try {
            // 서명 검증 설정
            Claims claims = Jwts.parser().verifyWith(secretKey)
                    .build().parseSignedClaims(token).getPayload();

            Date expiration = claims.getExpiration();
            String type = claims.get("type").toString();
            return expiration != null
                    && expiration.after(new Date())
                    && expectedType.equals(type);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰은 유효하지 않음
            return false;
        } catch (Exception e) {
            // 서명 불일치, 형식 오류 등 모두 false
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        return isTokenValid(token, "access");
    }

    public boolean isRefreshToken(String token) {
        return isTokenValid(token, "refresh");
    }

    // 아이디를 추출하는 메서드
    public String getUsername(String token) {
        String username = parseClaimsOrThrow(token).getSubject();
        if (username == null || username.isBlank()) throw new IllegalArgumentException("username missing");
        return username;
    }

    // id를 추출하는 메서드
    public Long getMemberId(String token) {
        Object v = parseClaimsOrThrow(token).get("memberId");
        if (v == null) throw new IllegalArgumentException("memberId missing");
        if (v instanceof Number n) return n.longValue();
        return Long.parseLong(v.toString());
    }

    // 이메일을 추출하는 메서드
    public String getEmail(String token) {
        String sub = parseClaimsOrThrow(token).get("email").toString();
        if (sub == null || sub.isBlank()) throw new IllegalArgumentException("subject missing");
        return sub;
    }

    // 권한을 추출하는 메서드
    public String getRole(String token) {
        return parseClaimsOrThrow(token).get("role").toString();
    }

}
