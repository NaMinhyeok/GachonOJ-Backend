package com.gachonoj.problemservice.utils.querycounter;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.sql.Connection;

@Aspect
@Component
@Slf4j
public class PerformanceAspect {
    private final ThreadLocal<ApiQueryCounter> queryCounter = new ThreadLocal<>();

    @Pointcut("execution(* javax.sql.DataSource.getConnection(..))")
    public void performancePointcut() {
    }

    @Around("performancePointcut()")
    public Object start(ProceedingJoinPoint point) throws Throwable {
        final Connection connection = (Connection) point.proceed();
        queryCounter.set(new ApiQueryCounter());
        final ApiQueryCounter counter = this.queryCounter.get();

        final Connection proxyConnection = getProxyConnection(connection, counter);
        queryCounter.remove();
        return proxyConnection;
    }

    private Connection getProxyConnection(Connection connection, ApiQueryCounter counter) {
        return (Connection) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Connection.class},
                new ConnectionHandler(connection, counter)
        );
    }
}
