package com.stephen.a2.authorization;

import com.stephen.a2.exception.UnAuthorizedException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    private TokenManager tokenManager;

    public AuthorizationInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod))
            return true;

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if (method.getAnnotation(Authorization.class) == null && handlerMethod.getBeanType().getAnnotation(Authorization.class) == null)
            return true;

        String token = request.getHeader("Authorization");
        TokenModel model = tokenManager.getToken(token);
        if (tokenManager.checkToken(model)) {
            request.setAttribute("userId", model.getUserId());
            return true;
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new UnAuthorizedException();
        }
    }
}
