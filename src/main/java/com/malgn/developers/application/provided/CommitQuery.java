package com.malgn.developers.application.provided;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.malgn.developers.application.provided.model.CommitQueryCriteria;
import com.malgn.developers.application.provided.model.CommitResult;

public interface CommitQuery {

    Page<CommitResult> search(CommitQueryCriteria criteria, Pageable pageable);

}
