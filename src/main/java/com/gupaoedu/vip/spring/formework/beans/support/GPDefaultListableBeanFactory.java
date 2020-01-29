package com.gupaoedu.vip.spring.formework.beans.support;

import com.gupaoedu.vip.spring.formework.beans.config.GPBeanDefinition;
import com.gupaoedu.vip.spring.formework.context.support.GPAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Discription:
 * @Author: Created by lyan on 2020/1/17 15:42
 */
public class GPDefaultListableBeanFactory extends GPAbstractApplicationContext {

    //存储注册信息的BeanDefinition ，伪IOC容器
    protected final Map<String, GPBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
}
