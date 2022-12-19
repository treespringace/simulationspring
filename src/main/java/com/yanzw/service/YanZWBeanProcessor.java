package com.yanzw.service;

import com.yanzw.spring.BeanPostProcessor;
import com.yanzw.spring.Component;

/**
 * @description:
 * @author: YanZW
 * @create: 2022-12-19 21:59
 **/
@Component("yanZWBeanProcessor")
public class YanZWBeanProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化前");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后");
        return bean;
    }
}
