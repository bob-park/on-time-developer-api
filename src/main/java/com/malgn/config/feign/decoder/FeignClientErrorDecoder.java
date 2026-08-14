package com.malgn.config.feign.decoder;

import static org.apache.commons.lang3.ObjectUtils.*;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;


import feign.Response;
import feign.Response.Body;
import feign.codec.ErrorDecoder;
import tools.jackson.databind.ObjectMapper;

import com.malgn.starter.common.exception.NotFoundException;
import com.malgn.starter.common.exception.ServiceRuntimeException;
import com.malgn.starter.common.exception.ServiceUnavailableException;

@Slf4j
@RequiredArgsConstructor
@Component
public class FeignClientErrorDecoder implements ErrorDecoder {

    private final ObjectMapper om;

    @Override
    public Exception decode(String s, Response response) {

        ProblemDetail error = null;

        try {

            Body body = response.body();

            if (isEmpty(body)) {
                return new ServiceRuntimeException();
            }

            error = om.readValue(body.asInputStream(), ProblemDetail.class);

        } catch (IOException e) {
            log.warn("Response parse Error - {}", e.getMessage());
        }

        if (isEmpty(error)) {
            return new ServiceRuntimeException();
        }

        switch (error.getStatus()) {

            case 400 -> {
                return new IllegalArgumentException(error.getDetail());
            }

            case 401 -> {
                return new AuthenticationServiceException(error.getDetail());
            }

            case 403 -> {
                return new AccessDeniedException(error.getDetail());
            }

            case 404 -> {
                return new NotFoundException(error.getDetail());
            }

            case 503 -> {
                String target = response.request().requestTemplate().feignTarget().name();

                return new ServiceUnavailableException(target);
            }

            default -> {
                return new ServiceRuntimeException(error.getDetail());
            }

        }

    }
}
