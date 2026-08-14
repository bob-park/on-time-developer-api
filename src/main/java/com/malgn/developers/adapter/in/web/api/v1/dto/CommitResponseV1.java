package com.malgn.developers.adapter.in.web.api.v1.dto;

import java.time.LocalDateTime;

import lombok.Builder;

import com.malgn.developers.application.provided.model.CommitResult;

@Builder
public record CommitResponseV1(String id,
                               String commitId,
                               String repo,
                               String branch,
                               String author,
                               String commitMessage,
                               LocalDateTime createdDate,
                               String createdBy,
                               LocalDateTime lastModifiedDate,
                               String lastModifiedBy) {

    public static CommitResponseV1 from(CommitResult result) {
        return CommitResponseV1.builder()
            .id(String.valueOf(result.id()))
            .commitId(result.commitId())
            .repo(result.repo())
            .branch(result.branch())
            .author(result.author())
            .commitMessage(result.commitMessage())
            .createdDate(result.createdDate())
            .createdBy(result.createdBy())
            .lastModifiedDate(result.lastModifiedDate())
            .lastModifiedBy(result.lastModifiedBy())
            .build();
    }
}
