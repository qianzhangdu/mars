package com.qianzhang.mars.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 基于代码形式的spring上下文工具类
 * @author qianzhang
 */
public class SpringAware implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    public SpringAware() {
    }

    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(String name) {
        try{
            T t = (T) applicationContext.getBean(name);
            return t;
        }catch (Exception e){
            return null;
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        try{
            T t = applicationContext.getBean(clazz);
            return t;
        }catch (Exception e){
            return null;
        }
    }

    public static <T> T registerBean(Class<T> c) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory();
        BeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName(c.getName());
        beanFactory.registerBeanDefinition(c.getName(), beanDefinition);
        return getBean(c.getName());
    }

    public static <T> T registerOrGet(Class<T> clazz) {
        T t = null;
        try {
            t = SpringAware.getBean(clazz);
        } catch (NoSuchBeanDefinitionException e) {
            if (t == null) {
                t = SpringAware.registerBean(clazz);
            }
        }
        return t;
    }
}
