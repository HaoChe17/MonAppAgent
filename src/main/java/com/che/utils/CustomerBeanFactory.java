package com.che.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by che on 2018/1/26.
 */
public class CustomerBeanFactory implements ApplicationContextAware {


    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(this.applicationContext == null) {
            this.applicationContext = applicationContext;
        }
    }
    //获取applicationContext
    public static ApplicationContext getApplicationContext() { return applicationContext; }
    //通过name获取 Bean.
    public static Object getBean(String name){ return getApplicationContext().getBean(name);}

//public class CustomerBeanFactory implements BeanFactoryAware {
//    private static BeanFactory bf;
//    @Override
//    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
//        bf = beanFactory;
//    }
//
//    public static <T> T getBean(String beanName){
//        return null != bf ? (T) bf.getBean(beanName) : null;
//    }
}
