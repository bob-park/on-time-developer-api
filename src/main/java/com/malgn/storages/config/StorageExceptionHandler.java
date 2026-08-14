package com.malgn.storages.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.malgn.starter.common.exception.ServiceRuntimeException;
import com.malgn.starter.common.exception.StandardProblemDetail;
import com.malgn.storages.domain.exception.DuplicatedStorageKeyException;
import com.malgn.storages.domain.exception.InvalidStorageKeyException;

/**
 * * Domain Exception Handler Example
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class StorageExceptionHandler {

    /**
     * 400 bad request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({InvalidStorageKeyException.class})
    public ProblemDetail handleBadRequest(ServiceRuntimeException e) {
        return StandardProblemDetail.createErrorDetail(e.getErrorCode(), e);
    }

    /**
     * 409
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({DuplicatedStorageKeyException.class})
    public ProblemDetail handleConflict(ServiceRuntimeException e) {
        return StandardProblemDetail.createErrorDetail(e.getErrorCode(), e);
    }

}
