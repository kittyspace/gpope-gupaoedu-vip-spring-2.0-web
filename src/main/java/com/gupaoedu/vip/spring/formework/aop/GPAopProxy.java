package com.gupaoedu.vip.spring.formework.aop;

/**
 * @Discription:
 * @Author: Created by lyan on 2020-02-01 16:19
 */
public interface GPAopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);

}
