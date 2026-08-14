package com.malgn.developers.adapter.in.web.api.v1.dto;

import java.time.LocalDateTime;

public record CommitSearchRequestV1(String repo,
                                    String branch,
                                    String author,
                                    String commitMessage,
                                    LocalDateTime createdDateFrom,
                                    LocalDateTime createdDateTo,
                                    String createdBy) {
}
