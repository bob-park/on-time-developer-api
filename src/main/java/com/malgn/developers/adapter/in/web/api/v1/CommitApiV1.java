package com.malgn.developers.adapter.in.web.api.v1;

import static com.malgn.developers.adapter.in.web.api.v1.dto.CommitResponseV1.*;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.malgn.developers.adapter.in.web.api.v1.dto.CommitExistsResponseV1;
import com.malgn.developers.adapter.in.web.api.v1.dto.CommitRegisterRequestV1;
import com.malgn.developers.adapter.in.web.api.v1.dto.CommitResponseV1;
import com.malgn.developers.adapter.in.web.api.v1.dto.CommitSearchRequestV1;
import com.malgn.developers.application.provided.CommitQuery;
import com.malgn.developers.application.provided.CommitRegister;
import com.malgn.developers.application.provided.model.CommitExistsResult;
import com.malgn.developers.application.provided.model.CommitQueryCriteria;
import com.malgn.developers.application.provided.model.CommitRegisterCommand;
import com.malgn.developers.application.provided.model.CommitResult;
import com.malgn.starter.lock.RedisNamedLockProvider;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "developers/commits", version = "1")
public class CommitApiV1 {

    private static final String LOCK_KEY_COMMIT_ID = "commit:lock:%s";

    private final CommitQuery commitQuery;
    private final CommitRegister commitRegister;

    private final RedisNamedLockProvider lockProvider;

    @PostMapping(path = "")
    public CommitResponseV1 register(@RequestBody @Valid CommitRegisterRequestV1 request) {

        CommitResult result =
            lockProvider.getLock(
                LOCK_KEY_COMMIT_ID.formatted(
                    request.commitId()),
                () -> commitRegister.register(
                    CommitRegisterCommand.builder()
                        .commitId(request.commitId())
                        .repo(request.repo())
                        .branch(request.branch())
                        .author(request.author())
                        .userUniqueId(request.userUniqueId())
                        .commitMessage(request.commitMessage())
                        .commitDate(request.commitDate())
                        .build()));

        return from(result);

    }

    @GetMapping(path = "")
    public PagedModel<CommitResponseV1> search(@Valid CommitSearchRequestV1 request,
        @PageableDefault(size = 25, sort = "commitDate", direction = Direction.DESC) Pageable pageable) {

        Page<CommitResult> result =
            commitQuery.search(
                CommitQueryCriteria.builder()
                    .repo(request.repo())
                    .branch(request.branch())
                    .author(request.author())
                    .userUniqueId(request.userUniqueId())
                    .commitMessage(request.commitMessage())
                    .commitDateFrom(request.commitDateFrom())
                    .commitDateTo(request.commitDateTo())
                    .build(),
                pageable);

        return new PagedModel<>(result.map(CommitResponseV1::from));
    }

    @GetMapping(path = "/exists/{commitId}")
    public CommitExistsResponseV1 exists(@PathVariable String commitId) {
        CommitExistsResult result =
            lockProvider.getLock(LOCK_KEY_COMMIT_ID.formatted(commitId), () -> commitQuery.exists(commitId));

        return CommitExistsResponseV1.from(result);
    }
}
