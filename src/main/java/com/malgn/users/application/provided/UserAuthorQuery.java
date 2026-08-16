package com.malgn.users.application.provided;

import com.malgn.users.application.provided.model.UserAuthorResult;

public interface UserAuthorQuery {
    UserAuthorResult getAuthor(String author);
}
