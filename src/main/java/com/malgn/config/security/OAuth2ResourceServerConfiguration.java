package com.malgn.config.security;

import static com.google.common.base.Preconditions.*;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.authorization.DefaultAuthorizationManagerFactory;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.web.cors.CorsConfigurationSource;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

import tools.jackson.databind.ObjectMapper;

import com.malgn.starter.auth.client.RoleClient;
import com.malgn.starter.auth.model.RoleResponse;
import com.malgn.starter.common.utils.role.RoleUtils;
import com.malgn.config.security.converter.JwtRoleGrantAuthoritiesConverter;
import com.malgn.starter.security.handler.RestAccessDeniedHandler;
import com.malgn.starter.security.handler.RestAuthenticationEntryPoint;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class OAuth2ResourceServerConfiguration {

    private final ObjectMapper om;

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain resourceSecurityFilterChain(HttpSecurity http) {

        http.authorizeHttpRequests(
            requests ->
                requests.anyRequest().authenticated());

        http.oauth2ResourceServer(
            resourceServer ->
                resourceServer.jwt(
                    jwtConfigurer ->
                        jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        http.exceptionHandling(
            exceptionHandler ->
                exceptionHandler.authenticationEntryPoint(authenticationEntryPoint())
                    .accessDeniedHandler(accessDeniedHandler()));

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource));

        return http.build();
    }

    /*
     * exception handling
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new RestAuthenticationEntryPoint(om);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new RestAccessDeniedHandler(om);
    }

    @Bean
    public JwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties) {
        checkArgument(StringUtils.isNotBlank(properties.getJwt().getIssuerUri()), "issuer uri must be provided");

        return JwtDecoders.fromOidcIssuerLocation(properties.getJwt().getIssuerUri());
    }

    /*
     * Authorization Manager
     */
    @Bean
    public AuthorizationManager<RequestAuthorizationContext> requestAuthorizationManager() {

        // TODO Please add an implementation of ConnectionBaseManager
        return AuthorizationManagers.allOf();
    }

    /**
     * role hierarchy
     */
    @Bean
    public RoleHierarchy roleHierarchy(RoleClient roleClient) {
        List<RoleResponse> roles = roleClient.getAll();
        Map<String, List<String>> roleMaps = RoleUtils.parseRoleHierarchyMap(roles);
        String rolesHierarchyStr = RoleUtils.fromHierarchy(roleMaps);

        log.debug("role hierarchy=\n{}", rolesHierarchyStr);

        return RoleHierarchyImpl.fromHierarchy(rolesHierarchyStr);
    }

    @Bean
    public static MethodSecurityExpressionHandler expressionHandler(RoleHierarchy roleHierarchy) {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();

        var authorizationManagerFactory = new DefaultAuthorizationManagerFactory<MethodInvocation>();
        authorizationManagerFactory.setRoleHierarchy(roleHierarchy);

        expressionHandler.setAuthorizationManagerFactory(authorizationManagerFactory);

        return expressionHandler;
    }

    /**
     * authentication converter
     *
     * @return authenticationConverter
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new JwtRoleGrantAuthoritiesConverter());

        return jwtAuthenticationConverter;
    }

}
