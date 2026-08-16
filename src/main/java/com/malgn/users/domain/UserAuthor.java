package com.malgn.users.domain;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang3.StringUtils.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.malgn.starter.common.entity.BaseEntity;
import com.malgn.starter.common.entity.annotation.SnowflakeIdGenerateValue;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users_authors")
public class UserAuthor extends BaseEntity<Long> {

    @Id
    @SnowflakeIdGenerateValue
    private Long id;

    private Long userUniqueId;
    private String author;

    @Builder
    private UserAuthor(Long id, Long userUniqueId, String author) {

        checkArgument(userUniqueId != null, "userUniqueId must be provided.");
        checkArgument(isNotBlank(author), "author must be provided.");

        this.id = id;
        this.userUniqueId = userUniqueId;
        this.author = author;
    }
}
