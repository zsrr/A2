package com.stephen.a2.exception;

import com.stephen.a2.response.BaseResponse;
import com.stephen.a2.response.ErrorDetail;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseRuntimeException {
    private static final long serialVersionUID = -4044388170173073929L;

    @Override
    public BaseResponse getBaseResponse() {
        ErrorDetail errorDetail = new ErrorDetail("Resource not found", NotFoundException.class, "资源不存在");
        return new BaseResponse(HttpStatus.NOT_FOUND, errorDetail);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
