package com.malgn.developers.application.required;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.malgn.developers.application.required.model.CommitQueryCondition;
import com.malgn.developers.domain.Commit;

public interface CommitRepository {

    Commit save(Commit commit);

    Optional<Commit> findCommit(Long id);

    Page<Commit> search(CommitQueryCondition condition, Pageable pageable);
}
