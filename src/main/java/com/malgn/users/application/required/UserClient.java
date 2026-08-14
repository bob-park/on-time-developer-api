package com.malgn.users.application.required;

import com.malgn.users.application.required.model.UserSummary;

public interface UserClient {

    UserSummary getUser(Long userUniqueId);

}
