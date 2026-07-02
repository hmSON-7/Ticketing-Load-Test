package com.ticket.backend.service;

import com.ticket.backend.config.redis.RefreshTokenStore;
import com.ticket.backend.config.security.JwtUtil;
import com.ticket.backend.db.entity.Member;
import com.ticket.backend.db.entity.Role;
import com.ticket.backend.db.repository.MemberRepository;
import com.ticket.backend.domain.dto.auth.JoinRequest;
import com.ticket.backend.domain.dto.auth.LoginRequest;
import com.ticket.backend.domain.dto.auth.LoginResponse;
import com.ticket.backend.exceptions.auth.InvalidRefreshTokenException;
import com.ticket.backend.exceptions.auth.MemberDuplicatedException;
import com.ticket.backend.exceptions.auth.MemberNotFoundException;
import com.ticket.backend.exceptions.auth.RefreshTokenReuseDetectionException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenStore refreshTokenStore;

    @Transactional
    public void join(JoinRequest joinRequest) {
        // 이미 존재하는 아이디 또는 이메일인가?
        if(memberRepository.existsByUsername(joinRequest.getUsername()))
            throw new MemberDuplicatedException("이미 등록된 아이디입니다.");

        if(memberRepository.existsByEmail(joinRequest.getEmail()))
            throw new MemberDuplicatedException("이미 등록된 이메일입니다.");

        // 비밀번호 암호화해서 DB에 계정 정보 등록
        Member member = new Member(
                joinRequest.getUsername(),
                passwordEncoder.encode(joinRequest.getPassword()),
                joinRequest.getEmail(),
                Role.USER
        );

        memberRepository.save(member);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Member member = memberRepository
                .findByUsername(loginRequest.getUsername())
                .orElse(null);
        if(member == null) throw new MemberNotFoundException("존재하지 않는 아이디입니다.");

        if(!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword()))
            throw new MemberNotFoundException("틀린 비밀번호를 입력하셨습니다.");

        // 로그인 인증 성공시 JWT 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(member);
        String refreshToken = jwtUtil.generateRefreshToken(member);

        // refresh 토큰 해시를 Redis에 저장(재발급 검증 기준). Redis 장애 시 503으로 실패.
        refreshTokenStore.save(member.getUsername(), refreshToken, jwtUtil.getRefreshTokenExpireSec());

        return new LoginResponse("bearer",
                accessToken, jwtUtil.getAccessTokenExpireSec(),
                refreshToken, jwtUtil.getRefreshTokenExpireSec()
        );
    }

    public LoginResponse reissue(String refreshToken) {
        // 1. refresh 토큰 자체 유효성(서명 + 만료 + 타입) 검증
        if (!jwtUtil.isRefreshToken(refreshToken))
            throw new InvalidRefreshTokenException("유효하지 않은 refresh 토큰입니다.");

        String username = jwtUtil.getUsername(refreshToken);

        // 2. 저장소의 해시와 비교 (Redis 장애 시 503)
        String storedHash = refreshTokenStore.getStoredHash(username);
        if (storedHash == null)
            throw new InvalidRefreshTokenException("만료되었거나 존재하지 않는 세션입니다.");

        // 3. 불일치 = 재사용 의심 → 강제 로그아웃 후 401
        if (!storedHash.equals(refreshTokenStore.hash(refreshToken))) {
            refreshTokenStore.delete(username);
            throw new RefreshTokenReuseDetectionException("refresh 토큰 재사용이 감지되었습니다. 다시 로그인해주세요.");
        }

        // 4. 사용자 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 아이디입니다."));

        // 5. 새 토큰 발급 + Redis 회전(새 해시로 교체)
        String newAccessToken = jwtUtil.generateAccessToken(member);
        String newRefreshToken = jwtUtil.generateRefreshToken(member);
        refreshTokenStore.save(username, newRefreshToken, jwtUtil.getRefreshTokenExpireSec());

        return new LoginResponse("bearer",
                newAccessToken, jwtUtil.getAccessTokenExpireSec(),
                newRefreshToken, jwtUtil.getRefreshTokenExpireSec()
        );
    }

    public void logout(String username) {
        // Redis의 refresh 토큰 키 삭제 (이후 재발급 불가)
        refreshTokenStore.delete(username);
    }

}
