package com.malgn.storages.adapter.out.aws.cloudfront;

import java.time.Duration;

import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import com.malgn.storages.application.required.StorageManager;

/**
 * TODO 구현 준비중
 */
@Slf4j
public class AwsCloudFrontStorageManager implements StorageManager {

    @PostConstruct
    public void init() {
        log.info("initialized AwsCloudFrontStorageManager...");
    }

    @Override
    public String generateUrl(String key, String customFilename, Duration duration) {
        return "";
    }
}
