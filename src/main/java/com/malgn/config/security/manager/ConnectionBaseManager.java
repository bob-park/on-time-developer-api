package com.malgn.config.security.manager;

import java.util.Collection;
import java.util.function.Supplier;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcher;

import org.jspecify.annotations.Nullable;

import com.malgn.starter.auth.model.type.RoleType;

public abstract class ConnectionBaseManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final GrantedAuthority GRANT_MANAGER = new SimpleGrantedAuthority(RoleType.ROLE_MANAGER.name());

    private final RequestMatcher requestMatcher;
    private final RoleHierarchy roleHierarchy;

    protected ConnectionBaseManager(RequestMatcher requestMatcher, RoleHierarchy roleHierarchy) {
        this.requestMatcher = requestMatcher;
        this.roleHierarchy = roleHierarchy;
    }

    @Override
    public @Nullable AuthorizationResult authorize(Supplier<? extends @Nullable Authentication> authentication,
        RequestAuthorizationContext requestContext) {

        Authentication auth = authentication.get();

        if (auth instanceof AnonymousAuthenticationToken) {
            return new AuthorizationDecision(false);
        }

        HttpServletRequest request = requestContext.getRequest();

        if (!matches(request)) {
            return null;
        }

        if (isManager(auth)) {
            return new AuthorizationDecision(true);
        }

        boolean access = vote(auth, request);

        return new AuthorizationDecision(access);
    }

    protected boolean isManager(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities =
            roleHierarchy.getReachableGrantedAuthorities(authentication.getAuthorities());

        return authorities.contains(GRANT_MANAGER);
    }

    private boolean matches(HttpServletRequest request) {
        return requestMatcher.matches(request);
    }

    public abstract boolean vote(Authentication authentication, HttpServletRequest request);

}
