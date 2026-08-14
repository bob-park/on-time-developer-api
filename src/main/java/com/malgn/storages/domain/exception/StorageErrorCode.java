package com.malgn.storages.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import org.springframework.http.HttpStatus;

import com.malgn.starter.common.exception.code.ErrorCode;

@ToString
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum StorageErrorCode implements ErrorCode {

    INVALID_STORAGE_KEY("INVALID_STORAGE_KEY", "Invalid storage key.", HttpStatus.BAD_REQUEST),
    DUPLICATED_STORAGE_KEY("DUPLICATED_STORAGE_KEY", "Duplicated storage key.", HttpStatus.CONFLICT),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;

}
