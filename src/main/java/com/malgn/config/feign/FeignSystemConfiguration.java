package com.malgn.config.feign;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

import feign.RequestInterceptor;

@RequiredArgsConstructor
@Configuration
public class FeignSystemConfiguration {

    private static final String AUTHORIZED_CLIENT_NAME = "keyflow-auth";

    @Bean
    public OAuth2AuthorizedClientManager systemAuthorizedClientManager(
        ClientRegistrationRepository clientRegistrationRepository,
        OAuth2AuthorizedClientService authorizedClientService) {

        OAuth2AuthorizedClientProvider provider =
            OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientService);
        manager.setAuthorizedClientProvider(provider);

        return manager;
    }

    @Bean
    public RequestInterceptor systemRequestInterceptor(OAuth2AuthorizedClientManager clientManager) {
        return requestTemplate -> requestTemplate.headers(getRequestHeaders(clientManager));
    }

    private Map<String, Collection<String>> getRequestHeaders(OAuth2AuthorizedClientManager clientManager) {

        Map<String, Collection<String>> headers = Maps.newHashMap();

        OAuth2AuthorizeRequest req =
            OAuth2AuthorizeRequest
                .withClientRegistrationId(AUTHORIZED_CLIENT_NAME)
                .principal(AUTHORIZED_CLIENT_NAME)
                .build();

        OAuth2AuthorizedClient client = clientManager.authorize(req);
        String token = client.getAccessToken().getTokenValue();

        if (StringUtils.isNotBlank(token)) {
            headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList("Bearer " + token));
        }

        headers.put(HttpHeaders.CONTENT_TYPE, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
        headers.put(HttpHeaders.ACCEPT, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));

        return headers;

    }

}
