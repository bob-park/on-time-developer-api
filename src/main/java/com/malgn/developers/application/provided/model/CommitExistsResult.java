package com.malgn.developers.application.provided.model;

import lombok.Builder;

@Builder
public record CommitExistsResult(boolean exists) {
}
