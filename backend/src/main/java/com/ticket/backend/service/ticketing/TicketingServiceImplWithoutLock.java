package com.ticket.backend.service.ticketing;

import com.ticket.backend.db.entity.TicketingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * 전략 ①: 락 없음 (순수 트랜잭션). 동시성 제어가 없어 오버셀을 재현하는 기준선.
 * 실제 로직은 3단계에서 구현.
 */
@Service
@Profile("nolock")
@RequiredArgsConstructor
public class TicketingServiceImplWithoutLock implements TicketingService {

    @Override
    public TicketingResult ticketing(Long memberId, Long ticketId) {
        // TODO(3단계): 조회 → 재고 검사 → 차감 → Ticketing 저장 (락 없음)
        throw new UnsupportedOperationException("nolock ticketing 미구현");
    }

}
