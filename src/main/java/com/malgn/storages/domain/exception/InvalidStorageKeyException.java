package com.malgn.storages.domain.exception;

import com.malgn.starter.common.exception.ServiceRuntimeException;
import com.malgn.starter.common.exception.code.ErrorCode;

public class InvalidStorageKeyException extends ServiceRuntimeException {

    private static final ErrorCode ERROR_CODE = StorageErrorCode.INVALID_STORAGE_KEY;

    public InvalidStorageKeyException() {
        super(ERROR_CODE);
    }

    public InvalidStorageKeyException(String key) {
        super(ERROR_CODE, ERROR_CODE.message() + " (key=" + key + ")");
    }
}
