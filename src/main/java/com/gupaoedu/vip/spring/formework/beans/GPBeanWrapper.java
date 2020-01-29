package com.gupaoedu.vip.spring.formework.beans;

/**
 * @Discription:
 * @Author: Created by lyan on 2020/1/17 17:59
 */
public class GPBeanWrapper {

    private Object wrappedInstance;

    public GPBeanWrapper(Object wrappedInstance){
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    // 返回代理以后的Class
    // 可能会是这个 $Proxy0
    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }
}
