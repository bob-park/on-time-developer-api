package com.malgn.developers.application.provided.model;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record CommitRegisterCommand(String commitId,
                                    String repo,
                                    String branch,
                                    String author,
                                    Long userUniqueId,
                                    String commitMessage,
                                    LocalDateTime commitDate) {
}
