package com.yanzw.spring;
/**
* @Description: 初始化bean之后执行逻辑，实现此接口即可，前提是实现类必须是spring管理类
* @Author: YanZw
* @Date: 2022-12-19
*/
public interface InitializingBean {
    void afterPropertiesSet();
}
