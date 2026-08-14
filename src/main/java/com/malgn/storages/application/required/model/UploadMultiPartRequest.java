package com.malgn.storages.application.required.model;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang3.ObjectUtils.*;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.*;

import java.time.Duration;

import lombok.Builder;

@Builder
public record UploadMultiPartRequest(String key,
                                     String uploadId,
                                     Integer partNumber,
                                     Duration expiration,
                                     Long uploadLength) {

    public UploadMultiPartRequest {
        checkArgument(isNotBlank(key), "key must be provided.");
        checkArgument(isNotBlank(uploadId), "uploadId must be provided.");
        checkArgument(isNotEmpty(partNumber), "partNumber must be provided.");
        checkArgument(isNotEmpty(uploadLength), "uploadLength must be provided.");

        expiration = getIfNull(expiration, Duration.ofMinutes(15));
    }

}
