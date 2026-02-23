package com.ticket.backend.db.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @Column(unique = true)
    private String code;

    @Column
    private String ticketName;

    @Column
    private int inventory;

    @Column
    private LocalDateTime openedAt;

    @Column
    private LocalDateTime closedAt;

    @Column
    @Enumerated(EnumType.STRING)
    private State state;

    /*@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<Ticketing> reservedList;*/

}
