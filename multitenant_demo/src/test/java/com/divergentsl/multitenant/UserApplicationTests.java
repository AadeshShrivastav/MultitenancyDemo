package com.divergentsl.multitenant;

import com.divergentsl.multitenant.config.TestJpaConfig;
import com.divergentsl.multitenant.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
    classes = TestApplication.class,
    properties = {
        "spring.main.allow-bean-definition-overriding=true"
    }
)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@Import({ TestJpaConfig.class, TestSecurityConfig.class })
@ContextConfiguration(classes = TestApplication.class)
class UserApplicationTests {
	@Test
    void contextLoads() {
        // Test that the application context loads successfully
    }
}
