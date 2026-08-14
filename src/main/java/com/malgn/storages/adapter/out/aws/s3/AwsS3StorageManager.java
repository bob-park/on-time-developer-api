package com.malgn.storages.adapter.out.aws.s3;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ContentDisposition;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import com.malgn.starter.common.exception.NotFoundException;
import com.malgn.storages.application.required.StorageManager;
import com.malgn.storages.application.required.model.AssetFileMetaResponse;

@Slf4j
@RequiredArgsConstructor
public class AwsS3StorageManager implements StorageManager {

    private static final String REGEX_PATTERN = "[^\\w가-힣]";
    private static final String REPLACEMENT = "_";

    private final String bucketName;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @PostConstruct
    public void init() {
        log.info("initialized AwsS3StorageManager...");
    }

    @Override
    public String generateUrl(String key, String customFilename, Duration duration) {

        String baseName = FilenameUtils.getBaseName(key);
        String extension = FilenameUtils.getExtension(key);

        if (StringUtils.isNotBlank(customFilename)) {
            baseName = FilenameUtils.getBaseName(customFilename);
        }

        String replaced = RegExUtils.replaceAll(baseName, REGEX_PATTERN, REPLACEMENT);
        String filename = replaced + "." + extension;

        return s3Presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                    .getObjectRequest(
                        GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .responseContentDisposition(
                                ContentDisposition.attachment()
                                    .filename(filename, StandardCharsets.UTF_8)
                                    .build()
                                    .toString())
                            .build())
                    .signatureDuration(duration)
                    .build())
            .url()
            .toString();
    }

    @Override
    public AssetFileMetaResponse getFileMeta(String key) {

        try {
            HeadObjectResponse result = s3Client.headObject(request -> request.bucket(bucketName).key(key));

            log.debug("s3 object meta. ({})", result);

            return AssetFileMetaResponse.builder()
                .key(key)
                .contentLength(result.contentLength())
                .lastModifiedDate(toLocalDateTime(result.lastModified()))
                .build();
        } catch (NoSuchKeyException e) {
            log.error("s3 object not found. ({})", key);
            throw new NotFoundException(key);
        }

    }

    private LocalDateTime toLocalDateTime(Instant instant) {

        if (instant == null) {
            return null;
        }

        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
