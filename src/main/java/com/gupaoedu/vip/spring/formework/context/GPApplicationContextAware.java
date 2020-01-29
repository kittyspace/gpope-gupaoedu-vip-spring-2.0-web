package com.gupaoedu.vip.spring.formework.context;

/**
 * @Discription:通过解耦方式获得ioc容器的顶层设计
 * 后面通过监听器取扫描所有的类，只要实现了此接口，
 * 将自动调用setApplicationContext()方法，从而将ioc容器注入到目标类中
 * @Author: Created by lyan on 2020/1/17 15:58
 */
public interface GPApplicationContextAware {

    void setApplicationContext(GPApplicationContext applicationContext) throws Exception;
}
