package com.gupaoedu.vip.spring.formework.context;

import com.gupaoedu.vip.spring.formework.annotation.GPAutowired;
import com.gupaoedu.vip.spring.formework.annotation.GPController;
import com.gupaoedu.vip.spring.formework.annotation.GPService;
import com.gupaoedu.vip.spring.formework.beans.GPBeanFactory;
import com.gupaoedu.vip.spring.formework.beans.GPBeanWrapper;
import com.gupaoedu.vip.spring.formework.beans.config.GPBeanDefinition;
import com.gupaoedu.vip.spring.formework.beans.support.GPBeanDefinitionReader;
import com.gupaoedu.vip.spring.formework.beans.support.GPDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
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

        

        //1 初始化
        GPBeanWrapper gpBeanWrapper = instantiateBean(beanName,beanDefinition );

        //为什么要把初始化和注入分开成两个步骤，不在一个方法里面？
        //class A{ B b;}
        //class B{ A a;}
        //先有鸡还是先有蛋的问题，一个方法是搞不定的，要分两次

        //2 拿到BeanWrapper之后，把BeanWrapper保存到IOC容器中去
//        if(this.factoryBeanInstanceCache.containsKey(beanName)){
//            throw new Exception("the" +beanName+" is exists!");
//        }
        this.factoryBeanInstanceCache.put(beanName,gpBeanWrapper);

        //3 注入
        populateBean( beanName, new GPBeanDefinition(),gpBeanWrapper);

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }


    private GPBeanWrapper instantiateBean(String beanName, GPBeanDefinition gpBeanDefinition) {
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
                this.singletonObjects.put(className,instance);
                //多存个没关系
                this.singletonObjects.put(gpBeanDefinition.getFactoryBeanName(),instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //3 把这个对象封装到BeanWrapper中
        GPBeanWrapper beanWrapper = new GPBeanWrapper(instance);
        // singletonObjects

        // factoryBeanInstanceCache

        //
        return beanWrapper;

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


}
