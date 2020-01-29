package com.gupaoedu.vip.spring.formework.context.support;

/**
 * @Discription: ioc容器实现的顶层设计 abstract类不能被new出来，要被继承才能生效
 * @Author: Created by lyan on 2020/1/17 15:36
 */
public abstract class GPAbstractApplicationContext {

    //受保护的，只提供给子类重写
    protected void refresh(){ }
}
