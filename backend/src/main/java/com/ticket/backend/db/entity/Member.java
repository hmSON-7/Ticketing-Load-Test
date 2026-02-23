package com.ticket.backend.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(unique=true)
    private String username;

    @Column
    private String password;

    @Column
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    /*@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Ticketing> ticketList;*/

    public void changePW(String password) {
        this.password = password;
    }

}
