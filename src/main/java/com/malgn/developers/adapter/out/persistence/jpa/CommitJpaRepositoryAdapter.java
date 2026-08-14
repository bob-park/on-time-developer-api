package com.malgn.developers.adapter.out.persistence.jpa;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.malgn.developers.application.required.CommitRepository;
import com.malgn.developers.application.required.model.CommitQueryCondition;
import com.malgn.developers.domain.Commit;

@RequiredArgsConstructor
@Repository
public class CommitJpaRepositoryAdapter implements CommitRepository {

    private final CommitJpaRepository commitRepository;

    @Override
    public Commit save(Commit commit) {
        return commitRepository.save(commit);
    }

    @Override
    public Optional<Commit> findCommit(Long id) {
        return commitRepository.findById(id);
    }

    @Override
    public Page<Commit> search(CommitQueryCondition condition, Pageable pageable) {
        return commitRepository.search(condition, pageable);
    }
}
