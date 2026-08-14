package com.malgn.developers.application.provided;

import com.malgn.developers.application.provided.model.CommitRegisterCommand;
import com.malgn.developers.application.provided.model.CommitResult;

public interface CommitRegister {

    CommitResult register(CommitRegisterCommand command);

}
