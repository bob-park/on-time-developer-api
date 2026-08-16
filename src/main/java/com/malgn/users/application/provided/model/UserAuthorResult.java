package com.malgn.users.application.provided.model;

import java.time.LocalDateTime;

import lombok.Builder;

import com.malgn.users.domain.UserAuthor;

@Builder
public record UserAuthorResult(Long id,
                               Long userUniqueId,
                               String author,
                               LocalDateTime createdDate,
                               String createdBy,
                               LocalDateTime lastModifiedDate,
                               String lastModifiedBy) {

    public static UserAuthorResult from(UserAuthor userAuthor) {
        return UserAuthorResult.builder()
            .id(userAuthor.getId())
            .userUniqueId(userAuthor.getUserUniqueId())
            .author(userAuthor.getAuthor())
            .createdDate(userAuthor.getCreatedDate())
            .createdBy(userAuthor.getCreatedBy())
            .lastModifiedDate(userAuthor.getLastModifiedDate())
            .lastModifiedBy(userAuthor.getLastModifiedBy())
            .build();
    }

}
