package com.malgn.storages.application.service;

import java.io.InputStream;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.malgn.storages.application.provided.AssetMultipartUploader;
import com.malgn.storages.application.provided.AssetUploader;
import com.malgn.storages.application.provided.model.AssetMultipartUploadCommand;
import com.malgn.storages.application.provided.model.AssetUploadCommand;
import com.malgn.storages.application.provided.model.AssetUploadCompletedPartCommand;
import com.malgn.storages.application.required.MultipartUploadManager;
import com.malgn.storages.application.required.UploadManager;
import com.malgn.storages.application.required.model.UploadCompletedPartRequest;
import com.malgn.storages.application.required.model.UploadMultiPartRequest;
import com.malgn.storages.application.required.model.UploadRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class AssetUploadService implements AssetMultipartUploader, AssetUploader {

    private final MultipartUploadManager multipartUploadManager;
    private final UploadManager uploadManager;

    @Override
    public String generateUploadId(String key) {
        return multipartUploadManager.generateUploadId(key);
    }

    @Override
    public String generatePresignedUrl(AssetMultipartUploadCommand command) {
        return multipartUploadManager.generatePresignedUrl(
            UploadMultiPartRequest.builder()
                .key(command.key())
                .uploadId(command.key())
                .uploadLength(command.uploadLength())
                .partNumber(command.partNumber())
                .expiration(command.expiration())
                .build());
    }

    @Override
    public void completedUpload(String key, String uploadId, List<AssetUploadCompletedPartCommand> parts) {

        multipartUploadManager.completedUpload(
            key,
            uploadId,
            parts.stream()
                .map(
                    item ->
                        UploadCompletedPartRequest.builder()
                            .eTag(item.eTag())
                            .partNumber(item.partNumber())
                            .build())
                .toList());

    }

    @Override
    public String generatedPresignedUrl(AssetUploadCommand command) {
        return uploadManager.generatedPresignedUrl(
            UploadRequest.builder()
                .key(command.key())
                .expiration(command.expiration())
                .build());
    }

    @Override
    public String putFile(String key, InputStream is, long contentLength) {
        return uploadManager.putFile(key, is, contentLength);
    }
}
