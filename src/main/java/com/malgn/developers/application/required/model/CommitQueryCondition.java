package com.malgn.developers.application.required.model;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record CommitQueryCondition(String repo,
                                   String branch,
                                   String author,
                                   Long userUniqueId,
                                   String commitMessage,
                                   LocalDateTime commitDateFrom,
                                   LocalDateTime commitDateTo) {
}
