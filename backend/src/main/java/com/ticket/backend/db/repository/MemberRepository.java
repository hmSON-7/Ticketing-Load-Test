package com.ticket.backend.db.repository;

import com.ticket.backend.db.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
