package com.malgn.storages.application.provided;

import java.io.InputStream;

import com.malgn.storages.application.provided.model.AssetUploadCommand;

public interface AssetUploader {

    String generatedPresignedUrl(AssetUploadCommand command);

    String putFile(String key, InputStream is, long contentLength);

}
