package com.malgn.storages.application.provided;

import java.util.List;

import com.malgn.storages.application.provided.model.AssetMultipartUploadCommand;
import com.malgn.storages.application.provided.model.AssetUploadCompletedPartCommand;

public interface AssetMultipartUploader {

    String generateUploadId(String key);

    String generatePresignedUrl(AssetMultipartUploadCommand command);

    void completedUpload(String key, String uploadId, List<AssetUploadCompletedPartCommand> parts);
}
