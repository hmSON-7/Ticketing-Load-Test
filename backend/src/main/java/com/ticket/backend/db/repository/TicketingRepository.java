package com.ticket.backend.db.repository;

import com.ticket.backend.db.entity.Ticketing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketingRepository extends JpaRepository<Ticketing, Long> {

}
