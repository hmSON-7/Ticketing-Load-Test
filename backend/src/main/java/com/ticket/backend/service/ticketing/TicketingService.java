package com.ticket.backend.service.ticketing;

import com.ticket.backend.db.entity.TicketingResult;

/**
 * 예매(티켓팅) 서비스. 동시성 제어 전략별 구현체를 @Profile로 선택한다.
 * (nolock / pessimistic / optimistic / redis)
 */
public interface TicketingService {

    /**
     * 특정 회원이 특정 티켓을 1매 예매한다.
     * @return SUCCESS / SOLD_OUT / ALREADY_PURCHASED
     */
    TicketingResult ticketing(Long memberId, Long ticketId);

}
