/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 19/05/2026
 * Usage    :
 * Since    : Version 1.0
 */
package com.me.learning.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain (HttpSecurity http) {
        http.authorizeHttpRequests (auth -> auth.anyRequest ().authenticated ()).formLogin (AbstractHttpConfigurer::disable)
                .httpBasic (Customizer.withDefaults ());
        return http.build ();
    }

    @Bean
    PasswordEncoder passwordEncoder () {
        //return PasswordEncoderFactories.createDelegatingPasswordEncoder ();
        return NoOpPasswordEncoder.getInstance ();
    }

    @Bean
    UserDetailsService userDetailsService () {
        UserDetails user = User.withUsername ("user").password ("{noop}user@321").roles ("USER").build ();
        UserDetails admin = User.withUsername ("admin").password ("admin@123").roles ("ADMIN").build ();
        return new InMemoryUserDetailsManager (user, admin);
    }

    @Bean
    CompromisedPasswordChecker compromisedPasswordChecker () {
        return new HaveIBeenPwnedRestApiPasswordChecker ();
    }
}
