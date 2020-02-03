package com.gupaoedu.vip.spring.formework.aop.asoect;

import com.gupaoedu.vip.spring.formework.aop.intercepter.GPMethodInterceptor;
import com.gupaoedu.vip.spring.formework.aop.intercepter.GPMethodInvocation;

import java.lang.reflect.Method;

/**
 * @Discription:
 * @Author: Created by lyan on 2020-02-02 0:05
 */
public class GPAfterReturningAdviceInterceptor extends GPAbstractAspectAdvice implements GPAdvice,GPMethodInterceptor {

    private GPJoinPoint joinPoint;

    public GPAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(GPMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }

}
