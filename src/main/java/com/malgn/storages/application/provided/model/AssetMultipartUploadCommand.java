package com.malgn.storages.application.provided.model;

import java.time.Duration;

import lombok.Builder;

@Builder
public record AssetMultipartUploadCommand(String key,
                                          String uploadId,
                                          Integer partNumber,
                                          Duration expiration,
                                          Long uploadLength) {
}
