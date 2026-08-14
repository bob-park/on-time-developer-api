package com.malgn.developers.application.provided.model;

import lombok.Builder;

@Builder
public record CommitRegisterCommand(String commitId,
                                    String repo,
                                    String branch,
                                    String author,
                                    String commitMessage) {
}
