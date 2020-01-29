package com.gupaoedu.vip.spring.formework.beans.config;

import lombok.Data;

/**
 * @Discription:
 * @Author: Created by lyan on 2020/1/17 15:47
 */
@Data
public class GPBeanDefinition {

    //类的名称
    private String beanClassName;
    //类是否是延时加载
    private Boolean lazyInit = false;
    //类在工厂里的名字
    private String factoryBeanName;
    //是否是单例（默认单例）
    private boolean isSingleton = true;


}
