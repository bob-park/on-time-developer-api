package com.malgn.developers.application.provided.model;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record CommitQueryCriteria(String repo,
                                  String branch,
                                  String author,
                                  String commitMessage,
                                  LocalDateTime createdDateFrom,
                                  LocalDateTime createdDateTo,
                                  String createdBy) {
}
