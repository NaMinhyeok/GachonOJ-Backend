package com.gachonoj.aiservice.utils.querycounter;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class ConnectionHandler implements InvocationHandler {
    private final Object target;
    private final ApiQueryCounter counter;

    public ConnectionHandler(Object target, ApiQueryCounter counter) {
        this.target = target;
        this.counter = counter;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        countPrepareStatement(method);
        logQueryCount(method);
        return method.invoke(target, args);
    }

    private void logQueryCount(Method method) {
        if (method.getName().equals("close")) {
            warnTooManyQuery();
            log.info("\n====== 쿼리 발생 갯수 : {} =======\n", counter.getQueryCount());
        }
    }

    private void countPrepareStatement(Method method) {
        if (method.getName().equals("prepareStatement")) {
            counter.increaseQueryCount();
        }
    }

    private void warnTooManyQuery() {
        if (counter.isWarn()) {
            log.warn("\n======= 너무 많은 쿼리가 실행 중입니다 !!!! =======");
        }
    }
}
