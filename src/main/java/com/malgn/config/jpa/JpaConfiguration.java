package com.malgn.config.jpa;

import java.util.Optional;

import jakarta.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.querydsl.jpa.impl.JPAQueryFactory;

@RequiredArgsConstructor
@EnableJpaAuditing
@Configuration
public class JpaConfiguration {

    @Bean
    public JPAQueryFactory queryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || auth instanceof AnonymousAuthenticationToken) {
                throw new AuthenticationServiceException("Authentication is required...");
            }

            return Optional.of(auth.getName());
        };

    }
}
