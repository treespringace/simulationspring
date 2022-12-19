package com.yanzw.spring;
/**
* @Description: bean初始化前后执行
* @Author: YanZw
* @Date: 2022-12-19
*/
public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean, String beanName);

    Object postProcessAfterInitialization(Object bean, String beanName);
}