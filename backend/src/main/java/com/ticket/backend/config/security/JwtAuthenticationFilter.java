package com.ticket.backend.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.header}")
    private String jwtHeader;

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(jwtHeader);

        // 표준 "Bearer"(대문자)와 "bearer"(소문자) 모두 허용 (대소문자 무시)
        if (StringUtils.hasText(authHeader)
                && authHeader.regionMatches(true, 0, "bearer", 0, "bearer".length())
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = authHeader.substring("bearer".length()).trim();
            // accessToken일 경우만 인증 처리
            try {
                if(!jwtUtil.isAccessToken(token))
                    throw new Exception("엑세스 토큰이 아닙니다.");
                String username = jwtUtil.getUsername(token);
                Long memberId = jwtUtil.getMemberId(token);
                String email = jwtUtil.getEmail(token);
                String role = jwtUtil.getRole(token);

                String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                List<GrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority(authority));

                JwtPrincipal principal = new JwtPrincipal(memberId, username, email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch(Exception e) {
                SecurityContextHolder.clearContext();
                log.debug("JWT 인증 실패: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/auth/join")
                || path.equals("/api/auth/login")
                || path.equals("/api/auth/reissue");
    }

    // Principal 구현 → authentication.getName()이 username을 반환하도록 보장
    public record JwtPrincipal(Long memberId, String username, String email) implements Principal {
        @Override
        public String getName() {
            return username;
        }
    }

}
