package com.malgn.users.adapter.in.web.api.v1.dto;

import java.time.LocalDateTime;

import lombok.Builder;

import com.malgn.users.application.provided.model.UserAuthorResult;

@Builder
public record UserAuthorResponseV1(String id,
                                   String userUniqueId,
                                   String author,
                                   LocalDateTime createdDate,
                                   String createdBy,
                                   LocalDateTime lastModifiedDate,
                                   String lastModifiedBy) {

    public static UserAuthorResponseV1 from(UserAuthorResult result){
        return UserAuthorResponseV1.builder()
            .id(String.valueOf(result.id()))
            .userUniqueId(String.valueOf(result.userUniqueId()))
            .author(result.author())
            .createdDate(result.createdDate())
            .createdBy(result.createdBy())
            .lastModifiedDate(result.lastModifiedDate())
            .lastModifiedBy(result.lastModifiedBy())
            .build();
    }
}
