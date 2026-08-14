package com.malgn.storages.application.required.model;

import lombok.Builder;

@Builder
public record UploadCompletedPartRequest(String eTag,
                                         Integer partNumber) {
}
