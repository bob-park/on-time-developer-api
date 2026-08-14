package com.malgn.storages.application.required;

import java.io.InputStream;

import com.malgn.storages.application.required.model.UploadRequest;

public interface UploadManager {

    String generatedPresignedUrl(UploadRequest request);

    String putFile(String key, InputStream is, long contentLength);
}
