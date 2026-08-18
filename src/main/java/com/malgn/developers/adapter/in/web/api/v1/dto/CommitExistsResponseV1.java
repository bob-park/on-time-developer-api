package com.malgn.developers.adapter.in.web.api.v1.dto;

import lombok.Builder;

import com.malgn.developers.application.provided.model.CommitExistsResult;

@Builder
public record CommitExistsResponseV1(boolean exists) {

    public static CommitExistsResponseV1 from(CommitExistsResult result) {
        return new CommitExistsResponseV1(result.exists());
    }
}
