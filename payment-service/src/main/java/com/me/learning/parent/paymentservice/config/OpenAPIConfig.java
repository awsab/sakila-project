package com.me.learning.parent.paymentservice.config;

import java.util.List;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Usage    : OpenAPI / Swagger configuration for payment-service
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI paymentserviceOpenAPI () {
        Server localServer = new Server ();
        localServer.setUrl ("http://localhost:8091");
        localServer.setDescription ("Local Development Server");

        Contact contact = new Contact ();
        contact.setName ("Prabakaran Ramu");
        contact.setEmail ("ramup@example.com");

        License license = new License ()
                .name ("MIT License")
                .url ("https://choosealicense.com/licenses/mit/");

        Info info = new Info ()
                .title ("payment-service API")
                .version ("1.0.0")
                .contact (contact)
                .description ("REST API for managing Sakila payment-service resources")
                .license (license);

        return new OpenAPI ()
                .info (info)
                .servers (List.of (localServer));
    }
}
