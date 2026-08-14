package com.malgn.developers.application.provided.model;

import java.time.LocalDateTime;

import lombok.Builder;

import com.malgn.developers.domain.Commit;

@Builder
public record CommitResult(Long id,
                           String commitId,
                           String repo,
                           String branch,
                           String author,
                           String commitMessage,
                           LocalDateTime createdDate,
                           String createdBy,
                           LocalDateTime lastModifiedDate,
                           String lastModifiedBy) {

    public static CommitResult from(Commit commit) {

        return CommitResult.builder()
            .id(commit.getId())
            .commitId(commit.getCommitId())
            .repo(commit.getRepo())
            .branch(commit.getBranch())
            .author(commit.getAuthor())
            .commitMessage(commit.getCommitMessage())
            .createdDate(commit.getCreatedDate())
            .createdBy(commit.getCreatedBy())
            .lastModifiedDate(commit.getLastModifiedDate())
            .lastModifiedBy(commit.getLastModifiedBy())
            .build();
    }

}
