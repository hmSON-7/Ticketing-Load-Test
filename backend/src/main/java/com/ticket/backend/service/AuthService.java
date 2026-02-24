package com.ticket.backend.service;

import com.ticket.backend.db.entity.Member;
import com.ticket.backend.db.entity.Role;
import com.ticket.backend.db.repository.MemberRepository;
import com.ticket.backend.domain.dto.auth.JoinRequest;
import com.ticket.backend.domain.dto.auth.LoginRequest;
import com.ticket.backend.domain.dto.auth.LoginResponse;
import com.ticket.backend.exceptions.MemberDuplicatedException;
import com.ticket.backend.exceptions.MemberNotFoundException;
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

    @Transactional
    public String join(JoinRequest joinRequest) {
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
        return member.getUsername();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Member member = memberRepository
                .findByUsername(loginRequest.getUsername())
                .orElse(null);
        if(member == null) throw new MemberNotFoundException("존재하지 않는 아이디입니다.");

        String password = passwordEncoder.encode(loginRequest.getPassword());
        if(!member.getPassword().equals(password))
            throw new MemberNotFoundException("틀린 비밀번호를 입력하셨습니다.");

        return new LoginResponse(
                member.getMemberId(),
                member.getUsername(),
                member.getEmail()
        );
    }

}
