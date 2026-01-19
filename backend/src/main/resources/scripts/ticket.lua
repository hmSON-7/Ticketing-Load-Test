-- ticket.lua
-- KEYS[1]: 재고 Key (예: ticket:inventory)
-- KEYS[2]: 구매자 목록 Set Key (예: ticket:purchasers)
-- KEYS[3]: 대기열 Queue List Key (예: ticket:queue)
-- ARGV[1]: 유저 ID (예: user:1001)

local inventoryKey = KEYS[1]
local purchasersKey = KEYS[2]
local queueKey = KEYS[3]
local userId = ARGV[1]

-- 1. 중복 구매 검사 (이미 산 사람인가?)
-- SISMEMBER 반환값: 1(존재함), 0(없음)
if redis.call('SISMEMBER', purchasersKey, userId) == 1 then
    return -1 -- Error Code: 이미 구매함
end

-- 2. 재고 검사 (재고가 남았는가?)
-- Redis의 숫자는 String으로 저장되므로 tonumber로 변환 필요
local stock = tonumber(redis.call('GET', inventoryKey) or "0")

if stock <= 0 then
    return 0 -- Error Code: 재고 없음 (Sold Out)
end

-- 3. 트랜잭션 실행 (원자성 보장 구간)
-- 재고 감소
redis.call('DECR', inventoryKey)
-- 구매자 명단에 등록 (중복 방지용)
redis.call('SADD', purchasersKey, userId)
-- 대기열 큐에 적재 (Worker가 가져갈 데이터)
redis.call('RPUSH', queueKey, userId)

return 1 -- Success Code: 구매 성공