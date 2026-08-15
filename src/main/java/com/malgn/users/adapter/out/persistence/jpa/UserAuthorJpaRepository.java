package com.malgn.users.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.malgn.users.adapter.out.persistence.jpa.query.UserAuthorJpaQueryRepository;
import com.malgn.users.domain.UserAuthor;

public interface UserAuthorJpaRepository extends JpaRepository<UserAuthor, Long>, UserAuthorJpaQueryRepository {
}
