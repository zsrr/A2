package com.stephen.a2.config;

import com.stephen.a2.authorization.AuthorizationInterceptor;
import com.stephen.a2.authorization.CurrentUserAnnotationResolver;
import com.stephen.a2.authorization.TokenManager;
import com.stephen.a2.authorization.TokenManagerImpl;
import com.stephen.a2.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import redis.clients.jedis.JedisPool;

import java.util.List;

public abstract class A2WebConfig extends WebMvcConfigurationSupport {

    @Bean(name = "tokenManager")
    public TokenManager tokenManager() {
        return new TokenManagerImpl(getJedisPool(), getSuffix(), getLastTime());
    }

    protected int getLastTime() {
        return 7 * 24 * 60 * 60;
    }

    protected String getSuffix() {
        return "-token";
    }

    protected abstract JedisPool getJedisPool();

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorizationInterceptor((TokenManager) getApplicationContext().getBean("tokenManager")));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new CurrentUserAnnotationResolver());
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new GlobalExceptionHandler());
    }
}
