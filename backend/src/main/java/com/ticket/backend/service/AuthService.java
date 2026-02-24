package com.ticket.backend.service;

import com.ticket.backend.db.entity.Member;
import com.ticket.backend.db.entity.Role;
import com.ticket.backend.db.repository.MemberRepository;
import com.ticket.backend.domain.dto.auth.JoinRequest;
import com.ticket.backend.exceptions.MemberDuplicatedException;
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
        Member duplicated = memberRepository
                .findByUsername(joinRequest.getUsername())
                .orElse(null);
        if(duplicated != null) throw new MemberDuplicatedException("이미 등록된 아이디입니다.");

        duplicated = memberRepository
                .findByEmail(joinRequest.getEmail())
                .orElse(null);
        if(duplicated != null) throw new MemberDuplicatedException("이미 등록된 이메일입니다.");

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

}
