package com.malgn.users.adapter.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.malgn.config.feign.FeignConfiguration;
import com.malgn.users.application.required.model.UserSummary;

@FeignClient(name = "authorization-server", contextId = "authorization-server-users", path = "api/v1/users", configuration = FeignConfiguration.class)
public interface UserFeignClient {

    @GetMapping(path = "{id:\\d+}/summary")
    UserSummary getUser(@PathVariable long id);
}
