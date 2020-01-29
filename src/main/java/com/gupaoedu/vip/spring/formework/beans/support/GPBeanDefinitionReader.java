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
            for (String className: registyBeanClasses){
                GPBeanDefinition gpBeanDefinition = doCreateBeanDefinition(className);
                if(null == gpBeanDefinition){
                    continue;
                }
                result.add(gpBeanDefinition);

            }
        return result;
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
