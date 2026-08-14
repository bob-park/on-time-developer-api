package com.malgn.users.adapter.out.feign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import com.malgn.starter.cache.CacheStore;
import com.malgn.starter.common.exception.ServiceRuntimeException;
import com.malgn.users.application.required.UserClient;
import com.malgn.users.application.required.model.UserSummary;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserFeignClientAdapter implements UserClient {

    private static final String KEY_USER_CLIENT_GET_USER = "users:%d:summary";

    private final UserFeignClient userClient;

    private final CacheStore cacheStore;

    @CircuitBreaker(name = "userClient", fallbackMethod = "getUserFallback")
    @Override
    public UserSummary getUser(Long userUniqueId) {
        UserSummary user = userClient.getUser(userUniqueId);

        cacheStore.put(getCacheKey(userUniqueId), user);

        return user;
    }

    private UserSummary getUserFallback(Long userUniqueId, Throwable e) {
        return cacheStore.get(getCacheKey(userUniqueId), UserSummary.class)
            .orElseThrow(() -> new ServiceRuntimeException(e.getMessage()));
    }

    private String getCacheKey(Long id) {
        return KEY_USER_CLIENT_GET_USER.formatted(id);
    }
}
