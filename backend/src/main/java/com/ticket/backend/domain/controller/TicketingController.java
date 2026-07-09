package com.ticket.backend.domain.controller;

import com.ticket.backend.config.security.JwtAuthenticationFilter.JwtPrincipal;
import com.ticket.backend.db.entity.TicketingResult;
import com.ticket.backend.service.ticketing.TicketingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/ticketing")
@RequiredArgsConstructor
public class TicketingController {

    // 활성 @Profile(nolock/pessimistic/optimistic/redis)에 따라 구현체가 주입됨
    private final TicketingService ticketingService;

    // 예매 (인증 필요). 회원 식별은 JWT principal에서.
    @PostMapping("/{ticketId}")
    public ResponseEntity<TicketingResult> ticketing(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal JwtPrincipal principal) {

        TicketingResult result = ticketingService.ticketing(principal.memberId(), ticketId);

        HttpStatus status = switch (result) {
            case SUCCESS -> HttpStatus.OK;
            case SOLD_OUT, ALREADY_PURCHASED -> HttpStatus.CONFLICT;
        };
        return ResponseEntity.status(status).body(result);
    }

}
