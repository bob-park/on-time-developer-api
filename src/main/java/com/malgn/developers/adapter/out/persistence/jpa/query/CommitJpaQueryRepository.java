package com.malgn.developers.adapter.out.persistence.jpa.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.malgn.developers.application.required.model.CommitQueryCondition;
import com.malgn.developers.domain.Commit;

public interface CommitJpaQueryRepository {

    Page<Commit> search(CommitQueryCondition condition, Pageable pageable);

}
