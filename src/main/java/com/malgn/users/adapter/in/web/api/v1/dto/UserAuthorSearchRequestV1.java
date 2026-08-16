package com.malgn.users.adapter.in.web.api.v1.dto;

import jakarta.validation.constraints.NotBlank;

public record UserAuthorSearchRequestV1(@NotBlank String author) {
}
