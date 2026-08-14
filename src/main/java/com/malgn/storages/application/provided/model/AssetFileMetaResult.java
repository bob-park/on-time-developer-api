package com.malgn.storages.application.provided.model;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record AssetFileMetaResult(String key,
                                  Long contentLength,
                                  LocalDateTime lastModifiedDate) {
}
