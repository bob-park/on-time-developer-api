package com.malgn.users.adapter.out.persistence.jpa;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import com.malgn.users.application.required.UserAuthorRepository;
import com.malgn.users.domain.UserAuthor;

@RequiredArgsConstructor
@Repository
public class UserAuthorJpaRepositoryAdapter implements UserAuthorRepository {

    private final UserAuthorJpaRepository userAuthorRepository;

    @Override
    public Optional<UserAuthor> findByAuthor(String author) {
        return userAuthorRepository.findByAuthor(author);
    }
}
