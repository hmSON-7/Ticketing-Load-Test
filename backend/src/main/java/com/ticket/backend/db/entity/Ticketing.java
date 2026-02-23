package com.ticket.backend.db.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Table(
    name = "ticketing",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_ticketing_member_ticket",
            columnNames = {"member_id", "ticket_id"}
        )
    }
)
public class Ticketing {

    // 2026.2.23 14:00 이슈
    // 1. 중복 구매를 DB가 막지 못하는 상태임. 복합 유니크 부재
    // 2. ManyToOne이 기본 EAGER 로딩이라 부하시 성능 악화
    // 3. ALlArgsConstructor -> 실수 유도 가능

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketingId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

}
