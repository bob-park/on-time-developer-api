package com.malgn.storages.application.required.model;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record AssetFileMetaResponse(String key,
                                    Long contentLength,
                                    LocalDateTime lastModifiedDate) {
}
