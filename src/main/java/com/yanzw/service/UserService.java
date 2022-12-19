package com.yanzw.service;

import com.yanzw.spring.Component;
import com.yanzw.spring.InitializingBean;

/**
 * @description:
 * @author: YanZW
 * @create: 2022-12-19 17:41
 **/
@Component(value = "userService")
public class UserService implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        System.out.println("执行afterPropertiesSet");
    }
}
