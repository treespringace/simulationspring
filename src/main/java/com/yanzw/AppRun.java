package com.yanzw;

import com.yanzw.service.AppConfig;
import com.yanzw.spring.YanzwApplicationContext;

/**
 * @description: 测试启动类
 * @author: YanZW
 * @create: 2022-12-19 17:04
 **/
public class AppRun {
    public static void main(String[] args) {
        YanzwApplicationContext yanzwApplicationContext = new YanzwApplicationContext(AppConfig.class);
        System.out.println(yanzwApplicationContext.getBean("userService"));
        System.out.println(yanzwApplicationContext.getBean("userService"));
    }
}
