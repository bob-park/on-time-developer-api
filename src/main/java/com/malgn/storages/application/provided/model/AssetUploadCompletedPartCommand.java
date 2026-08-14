package com.malgn.storages.application.provided.model;

import lombok.Builder;

@Builder
public record AssetUploadCompletedPartCommand(String eTag,
                                              Integer partNumber) {
}
