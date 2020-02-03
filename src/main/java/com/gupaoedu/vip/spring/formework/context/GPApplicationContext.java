package com.gupaoedu.vip.spring.formework.context;

import com.gupaoedu.vip.spring.formework.annotation.GPAutowired;
import com.gupaoedu.vip.spring.formework.annotation.GPController;
import com.gupaoedu.vip.spring.formework.annotation.GPService;
import com.gupaoedu.vip.spring.formework.aop.GPAopProxy;
import com.gupaoedu.vip.spring.formework.aop.GPCglibAopProxy;
import com.gupaoedu.vip.spring.formework.aop.GPJdkDynamicAopProxy;
import com.gupaoedu.vip.spring.formework.aop.config.GPAopConfig;
import com.gupaoedu.vip.spring.formework.aop.support.GPAdvisedSupport;
import com.gupaoedu.vip.spring.formework.core.GPBeanFactory;
import com.gupaoedu.vip.spring.formework.beans.GPBeanWrapper;
import com.gupaoedu.vip.spring.formework.beans.config.GPBeanDefinition;
import com.gupaoedu.vip.spring.formework.beans.config.GPBeanPostProcessor;
import com.gupaoedu.vip.spring.formework.beans.support.GPBeanDefinitionReader;
import com.gupaoedu.vip.spring.formework.beans.support.GPDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Discription:
 * 按照源码分析的套路：IOC,DI,MVC,AOP
 * @Author: Created by lyan on 2020/1/17 15:33
 */
public class GPApplicationContext extends GPDefaultListableBeanFactory implements GPBeanFactory {


    private String [] configLocations;
    private GPBeanDefinitionReader reader;

    //单例的IOC容器缓存
    private Map<String,Object> singletonObjects = new ConcurrentHashMap<>();
    //通用的IOC容器
    private Map<String,GPBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();



    public GPApplicationContext (String... configLocations){
        this.configLocations = configLocations;
        refresh();

    }
    public void refresh(){
        //1、定位配置文件
        reader = new GPBeanDefinitionReader(this.configLocations);
        //2、加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<GPBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3、注册 把配置信息放到容器里面（伪IOC容器）
        doRegisterBeanDefinition(beanDefinitions);

        //4、把不是延时加载的类，要提前初始化
        doAutowrited();
        
    }

    //只处理非延时加载的情况
    private void doAutowrited() {

        for (Map.Entry<String,GPBeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()){

            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().getLazyInit()){
                getBean(beanName);
            }

        }

    }

    private void doRegisterBeanDefinition(List<GPBeanDefinition> beanDefinitions) {
        for (GPBeanDefinition beanDefinition :beanDefinitions){
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
    }


    public Object getBean(Class<?> beanClass) {
        return getBean(beanClass.getName());
    }

    public Object getBean(String beanName) {

        GPBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        Object instance = null;

        //这个逻辑还不严谨，自己可以去参考spring源码
        //这里可以用工厂模式+策略模式
        //Processor是做什么的？类在初始化 前后后都做个通知/回调，比如到时候会扫描类，所有实现了InitArawe接口的都去触发通知,
        //根据业务场景的需要来，例：登录，登录的这个类只要创建了就把它存到缓存里面，这样一个动作。
        //这里是前置通知，跟aop还不一样，aop是后置通知；这是顶层设计，aop是应用层设计
        GPBeanPostProcessor postProcessor = new GPBeanPostProcessor();
        postProcessor.postProcessBeforeInitialization(instance,beanName);

        instance = instantiateBean(beanName, beanDefinition);

        //3 把这个对象封装到BeanWrapper中
        GPBeanWrapper beanWrapper = new GPBeanWrapper(instance);

        //创建一个代理的策略，看是用cglib还是用jdk
//        GPAopProxy proxy ;
//        Object prosy = proxy.getProxy();
//        createProxy()

        // singletonObjects
        // factoryBeanInstanceCache
        //1 初始化

        //为什么要把初始化和注入分开成两个步骤，不在一个方法里面？
        //class A{ B b;}
        //class B{ A a;}
        //先有鸡还是先有蛋的问题，一个方法是搞不定的，要分两次

        //2 拿到BeanWrapper之后，把BeanWrapper保存到IOC容器中去
//        if(this.factoryBeanInstanceCache.containsKey(beanName)){
//            throw new Exception("the" +beanName+" is exists!");
//        }
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);

        postProcessor.postProcessAfterInitialization(instance,beanName);

        //3 注入
        populateBean( beanName, new GPBeanDefinition(),beanWrapper);

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }


    private Object instantiateBean(String beanName, GPBeanDefinition gpBeanDefinition) {
        //1 拿到要实例化的对象的类名
        String className = gpBeanDefinition.getBeanClassName();

        //2 反射实例化，得到一个对象
        Object instance = null;
        try {
            //假设默认就是单例，细节暂且不考虑，先把主线拉通
            if(this.singletonObjects.containsKey(className)){
                instance = this.singletonObjects.get(className);
            }else{
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                GPAdvisedSupport config = instantionAopConfig(gpBeanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);

                //符合PointCut的规则的话，创建代理对象
                if(config.pointCutMatch()) {
                    instance = createProxy(config).getProxy();
                }

                this.singletonObjects.put(className,instance);
                //多存个没关系
                this.singletonObjects.put(gpBeanDefinition.getFactoryBeanName(),instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return instance;

    }

    private GPAopProxy createProxy(GPAdvisedSupport config) {
        Class<?> targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length > 0 ){
            return new GPJdkDynamicAopProxy(config);
        }
        return new GPCglibAopProxy(config);
    }

    private GPAdvisedSupport instantionAopConfig(GPBeanDefinition gpBeanDefinition) {

        GPAopConfig config = new GPAopConfig();

        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new GPAdvisedSupport(config);

    }

    private void populateBean(String beanName, GPBeanDefinition gpBeanDefinition, GPBeanWrapper gpBeanWrapper) {
        Object instance = gpBeanWrapper.getWrappedInstance();
//        gpBeanDefinition.getBeanClassName();
        Class<?> clazz = gpBeanWrapper.getWrappedClass();
        //判断只有加了注解的类，才执行依赖注入
        if(!(clazz.isAnnotationPresent(GPController.class) || clazz.isAnnotationPresent(GPService.class))){
            return;
        }
        
        //获得所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(GPAutowired.class)){
                continue;
            }
            GPAutowired autowired = field.getAnnotation(GPAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }
            //强制访问
            field.setAccessible(true);
            try {
                if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null){
                    continue;
                }
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public String[] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().toArray(new  String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }

}
