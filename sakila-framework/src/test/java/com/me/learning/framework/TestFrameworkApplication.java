package com.me.learning.framework;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

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
        DataJpaRepositoriesAutoConfiguration.class
})
class TestFrameworkApplication {
}
