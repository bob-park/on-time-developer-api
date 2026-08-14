package com.malgn.storages.application.service;

import java.time.Duration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.malgn.storages.application.provided.AssetStorageManager;
import com.malgn.storages.application.provided.model.AssetFileMetaResult;
import com.malgn.storages.application.required.StorageManager;
import com.malgn.storages.application.required.model.AssetFileMetaResponse;

@Slf4j
@RequiredArgsConstructor
@Service
public class AssetStorageService implements AssetStorageManager {

    private final StorageManager storageManager;

    @Override
    public String generateUrl(String key, String customFilename, Duration duration) {
        return storageManager.generateUrl(key, customFilename, duration);
    }

    @Override
    public AssetFileMetaResult getFileMeta(String key) {
        AssetFileMetaResponse response = storageManager.getFileMeta(key);

        return AssetFileMetaResult.builder()
            .key(response.key())
            .contentLength(response.contentLength())
            .lastModifiedDate(response.lastModifiedDate())
            .build();
    }
}
