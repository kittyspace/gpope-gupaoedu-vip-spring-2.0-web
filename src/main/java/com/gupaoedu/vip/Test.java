package com.gupaoedu.vip;

import com.gupaoedu.vip.spring.formework.context.GPApplicationContext;

/**
 * @Discription:
 * @Author: Created by lyan on 2020/1/22 14:56
 */
public class Test {

    public static void main(String[] args) {
        GPApplicationContext context = new GPApplicationContext("classpath:application.properties");
        Object myAction = context.getBean("myAction");
        System.out.println(context);
        System.out.println(myAction);
    }
}
