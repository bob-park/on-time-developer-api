package com.malgn.storages.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage.aws.s3")
public record AwsS3StorageProperties(String bucket) {
}
