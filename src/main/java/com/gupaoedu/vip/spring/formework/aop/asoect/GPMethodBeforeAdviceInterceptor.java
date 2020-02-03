package com.gupaoedu.vip.spring.formework.aop.asoect;

import com.gupaoedu.vip.spring.formework.aop.intercepter.GPMethodInterceptor;
import com.gupaoedu.vip.spring.formework.aop.intercepter.GPMethodInvocation;

import java.lang.reflect.Method;

/**
 * @Discription:
 * @Author: Created by lyan on 2020-02-02 0:05
 */
public class GPMethodBeforeAdviceInterceptor extends GPAbstractAspectAdvice implements GPAdvice,GPMethodInterceptor {

    private GPJoinPoint joinPoint;
    public GPMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
       super(aspectMethod,aspectTarget);
    }

    @Override
    public Object invoke(GPMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        this.joinPoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }

    private void before(Method method,Object[] args,Object target) throws Throwable{
        //传送了给织入参数
        //method.invoke(target);
        super.invokeAdviceMethod(this.joinPoint,null,null);

    }

}
