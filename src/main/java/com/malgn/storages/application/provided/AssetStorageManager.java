package com.malgn.storages.application.provided;

import java.time.Duration;

import com.malgn.storages.application.provided.model.AssetFileMetaResult;

public interface AssetStorageManager {

    Duration DEFAULT_EXPIRATION = Duration.ofMinutes(10);

    String generateUrl(String key, String customFilename, Duration duration);

    default String generateUrl(String key, Duration duration) {
        return generateUrl(key, null, duration);
    }

    default String generateUrl(String key) {
        return generateUrl(key, DEFAULT_EXPIRATION);
    }

    default String generateUrl(String key, String customFilename) {
        return generateUrl(key, customFilename, DEFAULT_EXPIRATION);
    }

    AssetFileMetaResult getFileMeta(String key);
}
