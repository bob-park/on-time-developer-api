package com.malgn.config.feign;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

import feign.RequestInterceptor;

@RequiredArgsConstructor
@Configuration
public class FeignConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.headers(getRequestHeaders());
    }

    private Map<String, Collection<String>> getRequestHeaders() {
        Map<String, Collection<String>> headers = Maps.newHashMap();

        String accessToken = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {

            Jwt jwt = (Jwt)authentication.getPrincipal();

            accessToken = jwt.getTokenValue();
        }

        if (StringUtils.isNotBlank(accessToken)) {
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList("Bearer " + accessToken));
        }

        headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
        headers.put(HttpHeaders.ACCEPT, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));

        return headers;
    }

}
