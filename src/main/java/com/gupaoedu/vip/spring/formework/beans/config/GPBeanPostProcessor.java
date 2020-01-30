package com.gupaoedu.vip.spring.formework.beans.config;

/**
 * @Discription:
 * @Author: Created by lyan on 2020-01-29 22:38
 */
public class GPBeanPostProcessor {

    //为在Bean的初始化前提供回调入口
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    //为在Bean的初始化之后提供回调入口
    public Object postProcessAfterInitialization(Object bean, String beanName)  {
        return bean;
    }


}
