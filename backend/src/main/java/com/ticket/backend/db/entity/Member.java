package com.ticket.backend.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class Member {

    // 2026.2.23 14:00 이슈
    // 1. nullable = false가 없어 데이터 무결성 약함
    // 2. ALlArgsConstructor -> 실수 유도 가능

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(unique=true, nullable = false)
    @NonNull
    private String username;

    @Column(nullable = false)
    @NonNull
    private String password;

    @Column(nullable = false)
    @NonNull
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NonNull
    private Role role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticketing> ticketList;

    public void changePW(String password) {
        this.password = password;
    }

}
