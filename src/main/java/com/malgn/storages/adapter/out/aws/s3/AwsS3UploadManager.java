package com.malgn.storages.adapter.out.aws.s3;

import java.io.InputStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import com.malgn.storages.application.required.UploadManager;
import com.malgn.storages.application.required.model.UploadRequest;

@Slf4j
@RequiredArgsConstructor
public class AwsS3UploadManager implements UploadManager {

    private final String bucketName;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Override
    public String generatedPresignedUrl(UploadRequest request) {

        PutObjectPresignRequest presignRequest =
            PutObjectPresignRequest.builder()
                .putObjectRequest(req -> req.bucket(bucketName).key(request.key()))
                .signatureDuration(request.expiration())
                .build();

        String result = s3Presigner.presignPutObject(presignRequest).url().toString();

        log.debug("generated presigned url. ({})", result);

        return result;
    }

    @Override
    public String putFile(String key, InputStream is, long contentLength) {

        s3Client.putObject(
            req ->
                req.bucket(bucketName).key(key),
            RequestBody.fromInputStream(is, contentLength));

        log.debug("put object to s3. (key: {})", key);

        return key;

    }
}
