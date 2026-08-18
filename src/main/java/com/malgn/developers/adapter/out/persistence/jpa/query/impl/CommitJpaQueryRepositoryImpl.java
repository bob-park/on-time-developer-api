package com.malgn.developers.adapter.out.persistence.jpa.query.impl;

import static com.malgn.developers.domain.QCommit.*;
import static org.apache.commons.lang3.ObjectUtils.*;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import org.apache.commons.lang3.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.malgn.developers.adapter.out.persistence.jpa.query.CommitJpaQueryRepository;
import com.malgn.developers.application.required.model.CommitQueryCondition;
import com.malgn.developers.domain.Commit;
import com.malgn.starter.common.querydsl.model.QueryDslPath;
import com.malgn.starter.common.querydsl.utils.QueryRepositoryUtils;

@RequiredArgsConstructor
public class CommitJpaQueryRepositoryImpl implements CommitJpaQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public Page<Commit> search(CommitQueryCondition condition, Pageable pageable) {

        List<Commit> content =
            query.selectFrom(commit)
                .where(mappingCondition(condition))
                .orderBy(sort(pageable))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        JPAQuery<Long> countQuery =
            query.select(commit.id.count())
                .from(commit)
                .where(mappingCondition(condition));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public boolean exists(String commitId) {

        Long count =
            query.select(commit.commitId.count())
                .from(commit)
                .where(commit.commitId.eq(commitId))
                .fetchOne();

        return getIfNull(count, 0L) > 0;
    }

    /*
     * mapping condition
     */
    private Predicate mappingCondition(CommitQueryCondition condition) {

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(containRepo(condition.repo()))
            .and(containBranch(condition.branch()))
            .and(containAuthor(condition.author()))
            .and(containCommitMessage(condition.commitMessage()))
            .and(goeFrom(condition.commitDateFrom()))
            .and(loeTo(condition.commitDateTo()))
            .and(eqUserUniqueId(condition.userUniqueId()));

        return builder;
    }

    private BooleanExpression containRepo(String repo) {
        return StringUtils.isNotBlank(repo) ? commit.repo.containsIgnoreCase(repo) : null;
    }

    private BooleanExpression containBranch(String branch) {
        return StringUtils.isNotBlank(branch) ? commit.branch.containsIgnoreCase(branch) : null;
    }

    private BooleanExpression containAuthor(String author) {
        return StringUtils.isNotBlank(author) ? commit.author.containsIgnoreCase(author) : null;
    }

    private BooleanExpression eqUserUniqueId(Long userUniqueId) {
        return userUniqueId != null ? commit.userUniqueId.eq(userUniqueId) : null;
    }

    private BooleanExpression containCommitMessage(String commitMessage) {
        return StringUtils.isNotBlank(commitMessage) ? commit.commitMessage.containsIgnoreCase(commitMessage) : null;
    }

    private BooleanExpression goeFrom(LocalDateTime from) {
        return from != null ? commit.commitDate.goe(from) : null;
    }

    private BooleanExpression loeTo(LocalDateTime to) {
        return to != null ? commit.commitDate.loe(to) : null;
    }

    /*
     * sort
     */
    private OrderSpecifier<?>[] sort(Pageable pageable) {
        return QueryRepositoryUtils.sort(
            pageable,
            List.of(
                new QueryDslPath<>("repo", commit.repo),
                new QueryDslPath<>("branch", commit.branch),
                new QueryDslPath<>("author", commit.author),
                new QueryDslPath<>("commitDate", commit.commitDate)));
    }
}
