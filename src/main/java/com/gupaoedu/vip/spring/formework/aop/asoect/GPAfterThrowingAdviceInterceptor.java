package com.gupaoedu.vip.spring.formework.aop.asoect;

import com.gupaoedu.vip.spring.formework.aop.intercepter.GPMethodInterceptor;
import com.gupaoedu.vip.spring.formework.aop.intercepter.GPMethodInvocation;

import java.lang.reflect.Method;

/**
 * @Discription:
 * @Author: Created by lyan on 2020-02-02 0:06
 */
public class GPAfterThrowingAdviceInterceptor extends GPAbstractAspectAdvice implements GPAdvice,GPMethodInterceptor {


    private String throwingName;

    public GPAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(GPMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Throwable e){
            invokeAdviceMethod(mi,null,e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName){
        this.throwingName = throwName;
    }
}
