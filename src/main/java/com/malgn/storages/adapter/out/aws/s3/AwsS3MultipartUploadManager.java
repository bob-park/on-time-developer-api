package com.malgn.storages.adapter.out.aws.s3;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import com.malgn.storages.application.required.MultipartUploadManager;
import com.malgn.storages.application.required.model.UploadCompletedPartRequest;
import com.malgn.storages.application.required.model.UploadMultiPartRequest;

@Slf4j
@RequiredArgsConstructor
public class AwsS3MultipartUploadManager implements MultipartUploadManager {

    private final String bucketName;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Override
    public String generateUploadId(String key) {

        CreateMultipartUploadResponse result =
            s3Client.createMultipartUpload(
                request ->
                    request.bucket(bucketName)
                        .key(key));

        log.debug("generated upload id. ({})", result.uploadId());

        return result.uploadId();
    }

    @Override
    public String generatePresignedUrl(UploadMultiPartRequest uploadMultiPartRequest) {

        UploadPartPresignRequest presignRequest =
            UploadPartPresignRequest.builder()
                .uploadPartRequest(
                    req ->
                        req.bucket(bucketName)
                            .key(uploadMultiPartRequest.key())
                            .uploadId(uploadMultiPartRequest.uploadId())
                            .partNumber(uploadMultiPartRequest.partNumber())
                            .build())
                .signatureDuration(uploadMultiPartRequest.expiration())
                .build();

        String result = s3Presigner.presignUploadPart(presignRequest).url().toString();

        log.debug("generated presigned url. ({})", result);

        return result;

    }

    @Override
    public void completedUpload(String key, String uploadId, List<UploadCompletedPartRequest> parts) {

        List<CompletedPart> completedParts =
            parts.stream()
                .map(part ->
                    CompletedPart.builder()
                        .partNumber(part.partNumber())
                        .eTag(part.eTag())
                        .build())
                .toList();

        s3Client.completeMultipartUpload(
            request ->
                request.bucket(bucketName)
                    .key(key)
                    .uploadId(uploadId)
                    .multipartUpload(
                        multiPart ->
                            multiPart.parts(completedParts).build()));

    }
}
