package com.malgn.developers.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.StringUtils;

import com.malgn.developers.application.provided.CommitQuery;
import com.malgn.developers.application.provided.model.CommitQueryCriteria;
import com.malgn.developers.application.provided.model.CommitResult;
import com.malgn.developers.application.required.CommitRepository;
import com.malgn.developers.application.required.model.CommitQueryCondition;
import com.malgn.developers.domain.Commit;
import com.malgn.starter.auth.access.AccessControl;
import com.malgn.starter.auth.access.AccessControlQueryRequest;
import com.malgn.starter.auth.access.RelationType;
import com.malgn.starter.auth.access.openfga.FgaObjectType;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommitQueryService implements CommitQuery {

    private final CommitRepository commitRepository;

    private final AccessControl accessControl;

    @Override
    public Page<CommitResult> search(CommitQueryCriteria criteria, Pageable pageable) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String createdBy = criteria.createdBy();

        if (StringUtils.isNotBlank(criteria.createdBy())) {

            // 조건에 createdBy 가 존재하는 경우 권한 체크
            boolean check =
                accessControl.check(
                    AccessControlQueryRequest.builder()
                        .userType(FgaObjectType.USER_TYPE)
                        .userId(auth.getName())
                        .relation(RelationType.CAN_READ)
                        .objectType(FgaObjectType.USER_TYPE)
                        .objectId(criteria.createdBy())
                        .build());

            if (!check) {
                throw new AuthorizationDeniedException("Access Denied.");
            }

        } else {
            createdBy = auth.getName();
        }

        Page<Commit> result =
            commitRepository.search(
                CommitQueryCondition.builder()
                    .repo(criteria.repo())
                    .branch(criteria.branch())
                    .author(criteria.author())
                    .commitMessage(criteria.commitMessage())
                    .createdDateFrom(criteria.createdDateFrom())
                    .createdDateTo(criteria.createdDateTo())
                    .createdBy(createdBy)
                    .build(),
                pageable);

        return result.map(CommitResult::from);
    }
}
