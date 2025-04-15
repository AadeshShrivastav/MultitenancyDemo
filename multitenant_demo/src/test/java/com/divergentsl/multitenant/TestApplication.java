package com.divergentsl.multitenant;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
}, scanBasePackages = {
        "com.divergentsl.multitenant.web",
        "com.divergentsl.multitenant.service",
        "com.divergentsl.multitenant.repository",
        "com.divergentsl.multitenant.entity",
        "com.divergentsl.multitenant.security", // Add security package
        "com.divergentsl.multitenant.dto"
})
@EntityScan("com.divergentsl.multitenant.entity")

public class TestApplication {
        // Empty test application class
}