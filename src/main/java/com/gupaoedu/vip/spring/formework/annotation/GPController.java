package com.gupaoedu.vip.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * @Discription:页面交互
 * @Author: Created by lyan on 2020/1/22 14:40
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPController {
    String value() default "";
}

