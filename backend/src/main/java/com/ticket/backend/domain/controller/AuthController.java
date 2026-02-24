package com.ticket.backend.domain.controller;

import com.ticket.backend.db.entity.Member;
import com.ticket.backend.domain.dto.auth.JoinRequest;
import com.ticket.backend.domain.dto.auth.LoginRequest;
import com.ticket.backend.domain.dto.auth.LoginResponse;
import com.ticket.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<Void> join(@Valid @RequestBody JoinRequest joinRequest) {
        authService.join(joinRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);

        // 차후 JWT 토큰까지 DTO에 추가
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

}
