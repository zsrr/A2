package com.stephen.a2.exception;


import com.stephen.a2.response.BaseResponse;
import com.stephen.a2.response.ErrorDetail;
import org.springframework.http.HttpStatus;

public class HttpParamResolveException extends BaseRuntimeException {
    private static final long serialVersionUID = 6107270659105507515L;

    private final String fieldName;
    private final String value;

    public HttpParamResolveException(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public BaseResponse getBaseResponse() {
        ErrorDetail ed = new ErrorDetail("Invalid parameter value", this.getClass(), "值为 \"" + value + "\" 的" + fieldName + "无法解析");
        return new BaseResponse(getHttpStatus(), ed);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
