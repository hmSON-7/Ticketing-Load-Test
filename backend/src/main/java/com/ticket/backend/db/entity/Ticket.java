package com.ticket.backend.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Check(constraints = "inventory >= 0")
public class Ticket {

    // 2026.2.23 14:00 이슈
    // 1. nullable = false가 없어 데이터 무결성 약함
    // 2. 시간 필드 및 상태 모델 불일치
    // 3. 재고 하한에 대한 보호 없음
    // 4, 특정 시간에 물리적인 상태 업데이트를 하려면 스케줄러 요구됨

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String ticketName;

    @PositiveOrZero
    @Column(nullable = false)
    private int inventory;

    @Column(nullable = false)
    private LocalDateTime openedAt;

    @Column(nullable = false)
    private LocalDateTime closedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticketing> reservedList;

    public void decreaseInventory(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("qty must be > 0");
        if (inventory < qty) throw new IllegalStateException("sold out");
        inventory -= qty;
    }

    @PrePersist
    @PreUpdate
    void validateInventory() {
        if (inventory < 0) throw new IllegalStateException("inventory must be >= 0");
    }

}
