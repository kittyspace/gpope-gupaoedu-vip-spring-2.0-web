package com.gupaoedu.vip.spring.formework.webmvc.servlet;

import lombok.Data;

import java.util.Map;

/**
 * @Discription:
 * @Author: Created by lyan on 2020-01-30 15:18
 */
@Data
public class GPModelAndView {

    private String viewName;
    private Map<String,?> model;

    public GPModelAndView(String viewName) { this.viewName = viewName; }

    public GPModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }


}
