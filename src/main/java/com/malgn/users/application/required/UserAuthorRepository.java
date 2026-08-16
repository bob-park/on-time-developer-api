package com.malgn.users.application.required;

import java.util.Optional;

import com.malgn.users.domain.UserAuthor;

public interface UserAuthorRepository {

    Optional<UserAuthor> findByAuthor(String author);

}
