package com.malgn.users.adapter.out.persistence.jpa.query.impl;

import static com.malgn.users.domain.QUserAuthor.*;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.malgn.users.adapter.out.persistence.jpa.query.UserAuthorJpaQueryRepository;
import com.malgn.users.domain.QUserAuthor;
import com.malgn.users.domain.UserAuthor;

@RequiredArgsConstructor
public class UserAuthorJpaQueryRepositoryImpl implements UserAuthorJpaQueryRepository {

    private final JPAQueryFactory query;

    @Override
    public Optional<UserAuthor> findByAuthor(String author) {
        return Optional.ofNullable(
            query.selectFrom(userAuthor)
                .where(userAuthor.author.eq(author))
                .fetchOne());
    }
}
