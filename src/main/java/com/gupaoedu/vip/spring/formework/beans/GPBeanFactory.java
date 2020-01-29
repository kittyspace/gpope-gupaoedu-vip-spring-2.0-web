package com.gupaoedu.vip.spring.formework.beans;

/**
 * @Discription: 单例工厂的顶层设计
 * @Author: Created by lyan on 2020/1/17 15:28
 */
public interface GPBeanFactory {

    /**
     * @author Created by lyan on 2020/1/17 15:30
     * @description 根据beanName从ioc容器中获取实例Bean
     * @param beanName
     * @return java.lang.Object
     **/
    Object getBean(String beanName);

    Object getBean(Class<?> clazz);


}
