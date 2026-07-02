package com.ticket.backend.config.redis;

import com.ticket.backend.exceptions.auth.RefreshTokenStoreUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

/**
 * Refresh 토큰 세션 저장소 (Redis).
 * key   = auth:refresh:{username}
 * value = sha256(refreshToken)  (원문 저장 금지)
 * TTL   = refresh 토큰 만료 시간
 * Redis 접근 실패는 503(RefreshTokenStoreUnavailableException)으로 변환한다.
 */
@Component
@RequiredArgsConstructor
public class RefreshTokenStore {

    private static final String KEY_PREFIX = "auth:refresh:";

    private final StringRedisTemplate redisTemplate;

    private String key(String username) {
        return KEY_PREFIX + username;
    }

    // 로그인/재발급 시 refresh 토큰 해시를 TTL과 함께 저장(회전 시에도 동일)
    public void save(String username, String refreshToken, long ttlSeconds) {
        try {
            redisTemplate.opsForValue()
                    .set(key(username), hash(refreshToken), Duration.ofSeconds(ttlSeconds));
        } catch (DataAccessException e) {
            throw new RefreshTokenStoreUnavailableException("refresh 토큰 저장소에 접근할 수 없습니다.");
        }
    }

    // 저장된 해시 조회 (없으면 null)
    public String getStoredHash(String username) {
        try {
            return redisTemplate.opsForValue().get(key(username));
        } catch (DataAccessException e) {
            throw new RefreshTokenStoreUnavailableException("refresh 토큰 저장소에 접근할 수 없습니다.");
        }
    }

    // 로그아웃 / 재사용 의심 강제 로그아웃
    public void delete(String username) {
        try {
            redisTemplate.delete(key(username));
        } catch (DataAccessException e) {
            throw new RefreshTokenStoreUnavailableException("refresh 토큰 저장소에 접근할 수 없습니다.");
        }
    }

    // SHA-256 해시(hex). 저장/비교에 동일하게 사용
    public String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 찾을 수 없습니다.", e);
        }
    }

}
