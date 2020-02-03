package com.gupaoedu.vip.spring.formework.aop.intercepter;

/**
 * @Discription:
 * @Author: Created by lyan on 2020-02-01 17:27
 */
public interface GPMethodInterceptor {

    Object invoke(GPMethodInvocation invocation) throws Throwable;

}
