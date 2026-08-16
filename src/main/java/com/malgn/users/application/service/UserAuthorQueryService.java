package com.malgn.users.application.service;

import static com.malgn.users.application.provided.model.UserAuthorResult.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.malgn.starter.common.exception.NotFoundException;
import com.malgn.users.application.provided.UserAuthorQuery;
import com.malgn.users.application.provided.model.UserAuthorResult;
import com.malgn.users.application.required.UserAuthorRepository;
import com.malgn.users.domain.UserAuthor;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserAuthorQueryService implements UserAuthorQuery {

    private final UserAuthorRepository userAuthorRepository;

    @Override
    public UserAuthorResult getAuthor(String author) {

        UserAuthor userAuthor =
            userAuthorRepository.findByAuthor(author)
            .orElseThrow(() -> new NotFoundException(UserAuthor.class, author));

        return from(userAuthor);
    }
}
