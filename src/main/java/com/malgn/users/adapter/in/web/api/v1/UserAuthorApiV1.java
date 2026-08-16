package com.malgn.users.adapter.in.web.api.v1;

import static com.malgn.users.adapter.in.web.api.v1.dto.UserAuthorResponseV1.*;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.malgn.users.adapter.in.web.api.v1.dto.UserAuthorResponseV1;
import com.malgn.users.adapter.in.web.api.v1.dto.UserAuthorSearchRequestV1;
import com.malgn.users.application.provided.UserAuthorQuery;
import com.malgn.users.application.provided.model.UserAuthorResult;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "users/authors", version = "1")
public class UserAuthorApiV1 {

    private final UserAuthorQuery userAuthorQuery;

    @GetMapping(path = "")
    public UserAuthorResponseV1 getAuthor(@Valid UserAuthorSearchRequestV1 request) {

        UserAuthorResult result = userAuthorQuery.getAuthor(request.author());

        return from(result);

    }

}
