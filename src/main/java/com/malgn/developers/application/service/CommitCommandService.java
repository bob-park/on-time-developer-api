package com.malgn.developers.application.service;

import static com.malgn.developers.application.provided.model.CommitResult.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.malgn.developers.application.provided.CommitRegister;
import com.malgn.developers.application.provided.model.CommitRegisterCommand;
import com.malgn.developers.application.provided.model.CommitResult;
import com.malgn.developers.application.required.CommitRepository;
import com.malgn.developers.domain.Commit;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CommitCommandService implements CommitRegister {

    private final CommitRepository commitRepository;

    @Override
    public CommitResult register(CommitRegisterCommand command) {

        Commit createdCommit =
            Commit.builder()
                .commitId(command.commitId())
                .repo(command.repo())
                .branch(command.branch())
                .author(command.author())
                .userUniqueId(command.userUniqueId())
                .commitMessage(command.commitMessage())
                .commitDate(command.commitDate())
                .build();

        createdCommit = commitRepository.save(createdCommit);

        log.debug("created commit. (id={})", createdCommit.getId());

        return from(createdCommit);
    }
}
