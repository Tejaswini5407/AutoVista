package com.autovista;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AutoVistaApplicationTests {

    @Test
    @DisplayName("Spring Application Context Loads Successfully")
    void contextLoads() {
        // Confirms Spring Boot beans wiring and dependencies resolve without failure
    }
}
