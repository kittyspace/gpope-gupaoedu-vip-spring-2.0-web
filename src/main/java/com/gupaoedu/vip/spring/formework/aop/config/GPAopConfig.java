package com.gupaoedu.vip.spring.formework.aop.config;

import lombok.Data;

/**
 * @Discription:
 * @Author: Created by lyan on 2020-02-01 20:13
 */
@Data
public class GPAopConfig {

    private String pointCut;
    private String aspectClass;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;

}
