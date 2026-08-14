package com.malgn.config.security;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
public class DocsSecurityConfiguration {

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain docsSecurityFilterChain(HttpSecurity http) {

        http.securityMatcher(
            PathPatternRequestMatcher.withDefaults()
                .matcher("/v1/docs/**"));

        http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
        http.cors(cors -> cors.configurationSource(corsConfigurationSource));

        return http.build();
    }
}
