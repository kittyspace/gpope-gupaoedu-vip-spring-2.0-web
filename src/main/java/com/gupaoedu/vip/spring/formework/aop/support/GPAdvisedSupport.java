package com.gupaoedu.vip.spring.formework.aop.support;

import com.gupaoedu.vip.spring.formework.aop.asoect.GPAfterReturningAdviceInterceptor;
import com.gupaoedu.vip.spring.formework.aop.asoect.GPAfterThrowingAdviceInterceptor;
import com.gupaoedu.vip.spring.formework.aop.asoect.GPMethodBeforeAdviceInterceptor;
import com.gupaoedu.vip.spring.formework.aop.config.GPAopConfig;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Discription:
 * @Author: Created by lyan on 2020-02-01 16:22
 */
@Data
public class GPAdvisedSupport {


    private Class<?> targetClass;
    private Object target;

    private GPAopConfig config;

    private Pattern pointCutClassPattern;

    private transient Map<Method, List<Object>> methodCache;

    public GPAdvisedSupport(GPAopConfig config) {

            this.config = config;
    }

    public Class<?> getTargetClass() {

        return this.targetClass;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method,  Class<?> targetClass)throws Exception{
        List<Object> cached = methodCache.get(method);
        if(cached == null){
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());

            cached = methodCache.get(m);

            //底层逻辑，对代理方法进行一个兼容处理
            this.methodCache.put(m,cached);
        }

        return cached;
    }

    public Object getTarget(){
        return this.target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();

    }

    private void parse() {

        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
        //pointCut=public .* com.gupaoedu.vip.spring.demo.service..*Service..*(.*)
        //玩正则
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));



        try {

            methodCache = new HashMap<Method, List<Object>>();
            Pattern pattern = Pattern.compile(pointCut);



            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String,Method>();
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(),m);
            }

            for (Method m : this.targetClass.getMethods()) {
                String methodString = m.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pattern.matcher(methodString);
                if(matcher.matches()){
                    //执行器链
                    List<Object> advices = new LinkedList<Object>();
                    //把每一个方法包装成 MethodIterceptor
                    //before
                    if(!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {
                        //创建一个Advivce
                        advices.add(new GPMethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectBefore()),aspectClass.newInstance()));
                    }
                    //after
                    if(!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        //创建一个Advivce
                        advices.add(new GPAfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfter()),aspectClass.newInstance()));
                    }
                    //afterThrowing
                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        //创建一个Advivce
                        GPAfterThrowingAdviceInterceptor throwingAdvice =
                                new GPAfterThrowingAdviceInterceptor(
                                        aspectMethods.get(config.getAspectAfterThrow()),
                                        aspectClass.newInstance());
                        throwingAdvice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                    }
                    methodCache.put(m,advices);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public boolean pointCutMatch() {

        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    public static void main(String[] args) {
        String sa ="public .* com.gupaoedu.vip.spring.demo.service..*Service..*(.*)";
        String s = sa.replaceAll("\\.", "\\\\.");
        System.out.println(s);
        String s1 = s.replaceAll("\\\\.\\*", ".*");
        System.out.println(s1);
        String s2 = s1.replaceAll("\\(", "\\\\(");
        System.out.println(s2);
        String s3 = s2.replaceAll("\\)", "\\\\)");
        System.out.println(s3);
    }
}
