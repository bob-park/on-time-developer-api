package com.malgn.storages.config;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import com.malgn.storages.adapter.out.aws.cloudfront.AwsCloudFrontStorageManager;
import com.malgn.storages.adapter.out.aws.s3.AwsS3MultipartUploadManager;
import com.malgn.storages.adapter.out.aws.s3.AwsS3StorageManager;
import com.malgn.storages.adapter.out.aws.s3.AwsS3UploadManager;
import com.malgn.storages.application.required.MultipartUploadManager;
import com.malgn.storages.application.required.StorageManager;
import com.malgn.storages.application.required.UploadManager;
import com.malgn.storages.config.properties.AwsS3StorageProperties;
import com.malgn.storages.config.properties.AwsStorageProperties;

@RequiredArgsConstructor
@EnableConfigurationProperties({AwsStorageProperties.class, AwsS3StorageProperties.class})
@Configuration
public class StorageConfiguration {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    private final AwsStorageProperties storageProperties;
    private final AwsS3StorageProperties s3Properties;

    @Bean
    @ConditionalOnProperty(prefix = "storage.aws", name = "type", havingValue = "s3_presigned_url", matchIfMissing = true)
    public StorageManager awsS3StorageManager() {
        return new AwsS3StorageManager(s3Properties.bucket(), s3Client, s3Presigner);
    }

    @Bean
    @ConditionalOnProperty(prefix = "atorage.aws", name = "type", havingValue = "cloudfront_signed_url")
    public StorageManager cloudFrontStorageManager() {
        return new AwsCloudFrontStorageManager();
    }

    @Bean
    public MultipartUploadManager awsS3MultipartUploadManager() {
        return new AwsS3MultipartUploadManager(s3Properties.bucket(), s3Client, s3Presigner);
    }

    @Bean
    public UploadManager awsS3UploadManager() {
        return new AwsS3UploadManager(s3Properties.bucket(), s3Client, s3Presigner);
    }

}
