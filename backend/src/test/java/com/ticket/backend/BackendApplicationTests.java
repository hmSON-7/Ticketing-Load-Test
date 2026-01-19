package com.ticket.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
    private RedisTemplate<String, String> redisTemplate;

	@Autowired
    private DataSource dataSource;

	@Test
    void contextLoads() throws SQLException {
        // 1. Redis 연결 테스트
        try {
            redisTemplate.opsForValue().set("test:key", "Redis Connection OK");
            String value = redisTemplate.opsForValue().get("test:key");
            System.out.println("✅ Redis Check: " + value);
            assertThat(value).isEqualTo("Redis Connection OK");
        } catch (Exception e) {
            System.err.println("❌ Redis Connection Failed: " + e.getMessage());
            throw e;
        }

        // 2. MySQL 연결 테스트
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("✅ MySQL Check: Connection Success!");
            assertThat(connection.isValid(1)).isTrue();
        } catch (Exception e) {
            System.err.println("❌ MySQL Connection Failed: " + e.getMessage());
            throw e;
        }
    }

}
