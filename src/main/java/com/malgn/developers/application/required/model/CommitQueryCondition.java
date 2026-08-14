package com.malgn.developers.application.required.model;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record CommitQueryCondition(String repo,
                                   String branch,
                                   String author,
                                   String commitMessage,
                                   LocalDateTime createdDateFrom,
                                   LocalDateTime createdDateTo,
                                   String createdBy) {
}
