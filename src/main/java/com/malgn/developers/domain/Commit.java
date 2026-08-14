package com.malgn.developers.domain;

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
@Table(name = "commits")
public class Commit extends BaseEntity<Long> {

    @Id
    @SnowflakeIdGenerateValue
    private Long id;

    private String commitId;
    private String repo;
    private String branch;
    private String author;
    private String commitMessage;

    @Builder
    private Commit(Long id, String commitId, String repo, String branch, String author, String commitMessage) {

        checkArgument(isNotBlank(commitId), "commitId must be provided.");
        checkArgument(isNotBlank(repo), "repo must be provided.");
        checkArgument(isNotBlank(branch), "branch must be provided.");
        checkArgument(isNotBlank(author), "author must be provided.");
        checkArgument(isNotBlank(commitMessage), "commitMessage must be provided.");

        this.id = id;
        this.commitId = commitId;
        this.repo = repo;
        this.branch = branch;
        this.author = author;
        this.commitMessage = commitMessage;
    }
}
