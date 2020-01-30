package com.gupaoedu.vip.spring.formework.beans.support;

import com.gupaoedu.vip.spring.formework.beans.config.GPBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Discription:
 * @Author: Created by lyan on 2020/1/17 16:27
 */
public class GPBeanDefinitionReader {

    //要进行注册的
    private List<String> registyBeanClasses = new ArrayList<String>();

    private Properties config = new Properties();
    //固定配置文件中的key,相当于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";


    public GPBeanDefinitionReader(String... locations){
        //通过url定位知道其所对应的文件，然后转为文件流读取
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
//        InputStream is = this.getClass().getResourceAsStream(locations[0].replace("classpath:",""));

        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {

        //将所有的包路径转换为文件路径，实际上就是把.替换为/就ok了
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        String path = null;
        try {
            //处理URL中文乱码
            path = URLDecoder.decode(url.getPath(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File dir = new File(path);
        for (File file : dir.listFiles()) {
            //如果是文件夹，继续递归
            if(file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            }else{
                registyBeanClasses.add(scanPackage + "." + file.getName().replace(".class", "").trim());
            }
        }
    }


    public Properties getConfig(){
        return this.config;
    }

    //把配置文件中扫描到的所有的配置信息转换为GPBeanDefinition对象，以便于之后IOC操作方便
    public List<GPBeanDefinition> loadBeanDefinitions(){
        List<GPBeanDefinition> result = new ArrayList<GPBeanDefinition>();
        try{
            for (String className: registyBeanClasses){
                Class<?> beanClass = Class.forName(className);
                //如果是一个接口，是不能实例化的
                //用它实现类来实例化
                if(beanClass.isInterface()) { continue; }

                //beanName有三种情况:
                //1、默认是类名首字母小写
                //2、自定义名字
                //3、接口注入
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()),beanClass.getName()));
//                result.add(doCreateBeanDefinition(beanClass.getName(),beanClass.getName()));

                Class<?> [] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    //如果是多个实现类，只能覆盖
                    //为什么？因为Spring没那么智能，就是这么傻
                    //这个时候，可以自定义名字
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    //把每一个配信息解析成一个BeanDefinition
    private GPBeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName){
        GPBeanDefinition beanDefinition = new GPBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    //把每一个配置信息解析成一个BeanDefinition
    private GPBeanDefinition doCreateBeanDefinition(String className){

        try{
             Class<?> beanClass = Class.forName(className);
             if(beanClass.isInterface()){
                 return null;
             }
             GPBeanDefinition beanDefinition = new GPBeanDefinition();
             beanDefinition.setBeanClassName(className);
             beanDefinition.setFactoryBeanName(toLowerFirstCase(beanClass.getSimpleName()));
             return beanDefinition;


         }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        chars[0] += 32;
        return  String.valueOf(chars);
    }

}
