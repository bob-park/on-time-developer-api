---
title: Exception Handling Conventions
scope: src/main/java/**/*.java
applies_to: adding a business error, a domain exception, or a new exception handler
related:
  - ./null-validation.md
  - ./web-api.md
  - ./naming.md
---

# Exception Handling Conventions

> Domain error codes, domain exceptions, and the global REST advice that maps them to RFC 9457 `ProblemDetail`. Read when adding a business error, a domain exception, or a new exception handler.

Business errors flow through three collaborating pieces per bounded context, plus one module-scoped REST advice. The HTTP status is carried by the error code and mirrored by `@ResponseStatus` on the status-grouped handler methods — never by `@ResponseStatus` on the exception itself.

## 1. Error Code Enum

Each bounded context declares a `{Domain}ErrorCode` enum implementing `com.malgn.starter.common.exception.code.ErrorCode`, in `domain.{name}.exception`. Each constant carries a stable `code` string, a default `message`, and the `HttpStatus` to return.

- Annotate exactly `@ToString @Getter @Accessors(fluent = true) @RequiredArgsConstructor`. `@Accessors(fluent = true)` makes the `ErrorCode` accessors read as `code()` / `message()` / `status()`.

```java
package com.malgn.domain.codes.exception;

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
public enum CodeErrorCode implements ErrorCode {

    DELETED_CODE(
        "DELETED_CODE",
        "Deleted code.",
        HttpStatus.BAD_REQUEST),
    DUPLICATE_CODE(
        "DUPLICATE_CODE",
        "Duplicate code. (parent + name)",
        HttpStatus.CONFLICT),
    CODE_HAS_CHILDREN(
        "CODE_HAS_CHILDREN",
        "Code has active children.",
        HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

}
```

## 2. Domain Exception

Each business error is a class extending `com.malgn.starter.common.exception.ServiceRuntimeException`, in `domain.{name}.exception`.

- Declare `private static final ErrorCode ERROR_CODE = {Domain}ErrorCode.XXX` and `private static final String DEFAULT_MESSAGE = "..."`.
- The no-arg constructor calls `super(ERROR_CODE, DEFAULT_MESSAGE)`.
- When the message should carry runtime state (e.g. an invalid status transition), add a constructor that formats the message and still passes `ERROR_CODE`.

```java
package com.malgn.domain.codes.exception;

import com.malgn.starter.common.exception.ServiceRuntimeException;
import com.malgn.starter.common.exception.code.ErrorCode;

public class DeletedCodeException extends ServiceRuntimeException {

    private static final ErrorCode ERROR_CODE = CodeErrorCode.DELETED_CODE;
    private static final String DEFAULT_MESSAGE = "deleted code.";

    public DeletedCodeException() {
        super(ERROR_CODE, DEFAULT_MESSAGE);
    }

}
```

```java
public class InvalidSessionChairStatusException extends ServiceRuntimeException {

    private static final ErrorCode ERROR_CODE = SessionChairErrorCode.INVALID_SESSION_CHAIR_STATUS;
    private static final String DEFAULT_MESSAGE = "Invalid session chair status transition.";

    public InvalidSessionChairStatusException(SessionChairStatus from, SessionChairStatus target) {
        super(ERROR_CODE, "%s from=%s, target=%s".formatted(DEFAULT_MESSAGE, from, target));
    }

}
```

## 3. Module Advice → ProblemDetail

Each module declares one `{Name}ExceptionHandler` (e.g., `StorageExceptionHandler`) in `{module}.config`, annotated `@Slf4j` + `@Order(Ordered.HIGHEST_PRECEDENCE)` + `@RestControllerAdvice`, mapping domain exceptions to `ProblemDetail` via `StandardProblemDetail.createErrorDetail(e.getErrorCode(), e)`.

- Handler methods are **grouped by HTTP status**: one method per status, annotated `@ResponseStatus` with that status.
- When you add a new domain exception, register it in the `@ExceptionHandler({...})` group whose status matches its `ErrorCode`'s `HttpStatus`; add a new status-group method if none exists.

```java
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

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class StorageExceptionHandler {

    /**
     * 400 bad request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({})
    public ProblemDetail handleBadRequest(ServiceRuntimeException e) {
        return StandardProblemDetail.createErrorDetail(e.getErrorCode(), e);
    }

    /**
     * 409
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({})
    public ProblemDetail handleConflict(ServiceRuntimeException e) {
        return StandardProblemDetail.createErrorDetail(e.getErrorCode(), e);
    }

}
```
