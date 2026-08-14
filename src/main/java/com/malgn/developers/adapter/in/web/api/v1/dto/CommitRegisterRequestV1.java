package com.malgn.developers.adapter.in.web.api.v1.dto;

import jakarta.validation.constraints.NotBlank;

public record CommitRegisterRequestV1(@NotBlank String commitId,
                                      @NotBlank String repo,
                                      @NotBlank String branch,
                                      @NotBlank String author,
                                      @NotBlank String commitMessage) {
}
