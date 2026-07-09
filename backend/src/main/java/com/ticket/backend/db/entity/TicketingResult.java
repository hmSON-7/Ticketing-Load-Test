package com.ticket.backend.db.entity;

/**
 * 예매 시도 결과. ticket.lua의 반환 코드(1/0/-1)와 정합.
 * SUCCESS(1) / SOLD_OUT(0) / ALREADY_PURCHASED(-1)
 */
public enum TicketingResult {
    SUCCESS,
    SOLD_OUT,
    ALREADY_PURCHASED
}
