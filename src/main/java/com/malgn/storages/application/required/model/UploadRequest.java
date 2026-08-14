package com.malgn.storages.application.required.model;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang3.ObjectUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.time.Duration;

import lombok.Builder;

@Builder
public record UploadRequest(String key,
                            Duration expiration) {

    public UploadRequest {
        checkArgument(isNotBlank(key), "key must be provided.");

        expiration = getIfNull(expiration, Duration.ofMinutes(15));
    }
}
