package com.gupaoedu.vip.spring.demo.aspect;

import com.gupaoedu.vip.spring.formework.aop.asoect.GPJoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @Discription:
 * @Author: Created by lyan on 2020-02-01 16:14
 */
@Slf4j
public class LogAspect {

    public void before(GPJoinPoint joinPoint){

        //往对象里面记录调用的开始时间
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(),System.currentTimeMillis());
        //这个方法中的逻辑，是由我们自己写的
        log.info("Invoker Before Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));

    }

    public void after(GPJoinPoint joinPoint){
        log.info("Invoker After Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("use time :" + (endTime - startTime));

    }

    public void afterThrowing(GPJoinPoint joinPoint, Throwable ex){

        //异常监测，我可以拿到异常的信息
        log.info("出现异常" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()) +
                "\nThrows:" + ex.getMessage());

    }


}
