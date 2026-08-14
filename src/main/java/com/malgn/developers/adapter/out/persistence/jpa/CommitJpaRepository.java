package com.malgn.developers.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.malgn.developers.adapter.out.persistence.jpa.query.CommitJpaQueryRepository;
import com.malgn.developers.domain.Commit;

public interface CommitJpaRepository extends JpaRepository<Commit, Long>, CommitJpaQueryRepository {
}
