package com.me.learning.framework;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Minimal Spring Boot application used exclusively for integration tests
 * of the awsab-framework auto-configuration.
 *
 * <p>JPA/datasource auto-configuration is excluded because the framework
 * is a library – it does not ship its own datasource. Each consuming
 * service provides its own database configuration.
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
})
class TestFrameworkApplication {
}

