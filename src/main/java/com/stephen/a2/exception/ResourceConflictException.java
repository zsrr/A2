package com.stephen.a2.exception;

import com.stephen.a2.response.BaseResponse;
import com.stephen.a2.response.ErrorDetail;
import org.springframework.http.HttpStatus;

public class ResourceConflictException extends BaseRuntimeException {
    private static final long serialVersionUID = 5066951808608110092L;

    @Override
    public BaseResponse getBaseResponse() {
        ErrorDetail errorDetail = new ErrorDetail("Resource conflict", this.getClass(), "资源定义发生冲突");
        return new BaseResponse(HttpStatus.CONFLICT, errorDetail);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
