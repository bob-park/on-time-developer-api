package com.malgn.storages.application.required;

import java.util.List;

import com.malgn.storages.application.required.model.UploadCompletedPartRequest;
import com.malgn.storages.application.required.model.UploadMultiPartRequest;

public interface MultipartUploadManager {

    String generateUploadId(String key);

    String generatePresignedUrl(UploadMultiPartRequest uploadMultiPartRequest);

    void completedUpload(String key, String uploadId, List<UploadCompletedPartRequest> parts);

}
