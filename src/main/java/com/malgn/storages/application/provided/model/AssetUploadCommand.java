package com.malgn.storages.application.provided.model;

import java.time.Duration;

public record AssetUploadCommand(String key,
                                 Duration expiration) {
}
