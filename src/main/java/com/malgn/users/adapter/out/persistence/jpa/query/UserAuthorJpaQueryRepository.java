package com.malgn.users.adapter.out.persistence.jpa.query;

import java.util.Optional;

import com.malgn.users.domain.UserAuthor;

public interface UserAuthorJpaQueryRepository {

    Optional<UserAuthor> findByAuthor(String author);

}
