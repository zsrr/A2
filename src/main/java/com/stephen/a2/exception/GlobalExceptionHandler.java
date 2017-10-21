package com.stephen.a2.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.stephen.a2.response.ErrorDetail;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.*;
import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

public class GlobalExceptionHandler implements HandlerExceptionResolver {

    private static final Map<Class<? extends Throwable>, Integer> SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP = new HashMap<>(16);

    static {
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(NoSuchRequestHandlingMethodException.class, SC_NOT_FOUND);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(HttpRequestMethodNotSupportedException.class, SC_METHOD_NOT_ALLOWED);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(HttpMediaTypeNotSupportedException.class, SC_UNSUPPORTED_MEDIA_TYPE);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(HttpMediaTypeNotAcceptableException.class, SC_NOT_ACCEPTABLE);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(MissingPathVariableException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(MissingServletRequestParameterException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(ServletRequestBindingException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(ConversionNotSupportedException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(TypeMismatchException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(HttpMessageNotReadableException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(HttpMessageNotWritableException.class, SC_INTERNAL_SERVER_ERROR);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(MethodArgumentNotValidException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(MissingServletRequestPartException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(BindException.class, SC_BAD_REQUEST);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(NoHandlerFoundException.class, SC_NOT_FOUND);
        SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.put(AsyncRequestTimeoutException.class, SC_SERVICE_UNAVAILABLE);
    }

    public static int getStatusCode(Class<? extends Throwable> clazz) {
        if (SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.containsKey(clazz)) {
            return SPRING_STANDARD_EXCEPTION_STATUS_CODE_MAP.get(clazz);
        }
        return 500;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse,
                                         Object o, Exception e) {
        ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
        if (e instanceof BaseRuntimeException) {
            BaseRuntimeException transform = (BaseRuntimeException) e;
            httpServletResponse.setStatus(transform.getHttpStatus().value());
            handleBaseRuntimeResponse(mav, transform);
        } else if (e instanceof ConstraintViolationException) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            handleConstraintViolation(mav, (ConstraintViolationException) e);
        } else if (e instanceof JsonProcessingException) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            handleJsonProcessingException(mav, (JsonProcessingException) e);
        } else {
            httpServletResponse.setStatus(getStatusCode(e.getClass()));
            handleException(mav, e);
        }
        return mav;
    }

    private void handleException(ModelAndView mav, Exception e) {
        mav.addObject("status", getStatusCode(e.getClass()));
        mav.addObject("error", new ErrorDetail("Exception happened", e.getClass(), e.getMessage()));
    }

    private void handleJsonProcessingException(ModelAndView mav, JsonProcessingException e) {
        mav.addObject("status", HttpStatus.BAD_REQUEST.value());
        mav.addObject("error", new ErrorDetail("Json is invalid", JsonProcessingException.class, e.getMessage()));
    }

    private void handleConstraintViolation(ModelAndView mav, ConstraintViolationException e) {
        mav.addObject("status", HttpStatus.BAD_REQUEST.value());
        mav.addObject("error", new ErrorDetail("Constraint not met", ConstraintViolationException.class, e.getMessage()));
    }

    private void handleBaseRuntimeResponse(ModelAndView mav, BaseRuntimeException e) {
        mav.addObject("status", e.getBaseResponse().getStatus());
        mav.addObject("error", e.getBaseResponse().getError());
    }
}
