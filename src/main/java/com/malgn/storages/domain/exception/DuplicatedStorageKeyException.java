package com.malgn.storages.domain.exception;

import com.malgn.starter.common.exception.ServiceRuntimeException;
import com.malgn.starter.common.exception.code.ErrorCode;

public class DuplicatedStorageKeyException extends ServiceRuntimeException {

    private static final ErrorCode ERROR_CODE = StorageErrorCode.DUPLICATED_STORAGE_KEY;

    public DuplicatedStorageKeyException() {
        super(ERROR_CODE);
    }

    public DuplicatedStorageKeyException(String key) {
        super(ERROR_CODE, ERROR_CODE.message() + " (key=" + key + ")");
    }
}
