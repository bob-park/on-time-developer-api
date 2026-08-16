package com.malgn.developers.adapter.in.web.api.v1.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommitRegisterRequestV1(@NotBlank String commitId,
                                      @NotBlank String repo,
                                      @NotBlank String branch,
                                      @NotBlank String author,
                                      @NotNull Long userUniqueId,
                                      @NotBlank String commitMessage,
                                      @NotNull LocalDateTime commitDate) {
}
