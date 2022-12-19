package com.yanzw.spring;

import com.yanzw.spring.utils.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 容器类
 * @author: YanZW
 * @create: 2022-12-19 17:07
 **/
public class YanzwApplicationContext {
    private Class configClass;
    private ConcurrentHashMap<String, Object> singleObjects = new ConcurrentHashMap<>();//单例池
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private List<BeanPostProcessor> beanPostProcessorList = new Vector<>();
    private static final String SINGLETON_BEAN = "singleton";

    public YanzwApplicationContext(Class configClass) {
        //解析配置类
        //ComponentScan注解--》获取描路径--》扫描---》BeanDefinition->BeanDefinitionMap
        scan(configClass);
        //创建bean
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals(SINGLETON_BEAN)) {
                singleObjects.put(beanName,creatBean(beanName,beanDefinition));
            }
        }
    }
    private Object creatBean(String beanName , BeanDefinition beanDefinition){
        Class clazz = beanDefinition.getClazz();
        try {
            Object newInstance = clazz.getDeclaredConstructor().newInstance();
            //依赖注入  对属性进行赋值
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    //比较简单的实现
                    Object bean = getBean(beanName);
                    declaredField.setAccessible(true);
                    declaredField.set(newInstance,bean);
                }
            }
            if(newInstance instanceof BeanNameAware){
                ((BeanNameAware)newInstance).setBeanName(beanName);
            }
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                newInstance = beanPostProcessor.postProcessBeforeInitialization(newInstance,beanName);
            }
            if(newInstance instanceof InitializingBean){
                ((InitializingBean)newInstance).afterPropertiesSet();
            }
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                newInstance = beanPostProcessor.postProcessAfterInitialization( newInstance,beanName);
            }
            return newInstance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void scan(Class configClass) {
        this.configClass = configClass;
        //解析配置类
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        //获取注解类的包路径
        String value = componentScanAnnotation.value();
        //扫描
        //Bootstrap--->jre/lib
        //Ext ------> jre/ext/lib
        //classpath --->app 应用层


        //获取应用层 应用类的类加载器
        ClassLoader appClassLoader = YanzwApplicationContext.class.getClassLoader();
        URL resource = appClassLoader.getResource(value.replace(".", "/"));
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                try {
                    String absolutePath = f.getAbsolutePath();
                    String className = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
                    className = className.replace("\\", ".");
                    Class<?> clazz = appClassLoader.loadClass(className);
                    if (clazz.isAnnotationPresent(Component.class)) {

                        //判断作用域，即单例还是多例
                        //BeanDefinition  核心
                        Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                        String beanName = componentAnnotation.value();
                        if (StringUtils.isEmpty(beanName)) {
                            beanName = className;
                        }
                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setClazz(clazz);
                        if (BeanPostProcessor.class.isAssignableFrom(clazz)) {//当前类是否实现了BeanPostProcessor
                            //如果实现了则实例化该对象，其实真正的spring底层并不是用此方式实例化，而是通过getBean的方式
                            BeanPostProcessor instance = (BeanPostProcessor)clazz.getDeclaredConstructor().newInstance();
                            beanPostProcessorList.add(instance);
                        }
                        if (clazz.isAnnotationPresent(Scope.class)) {
                            Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                        } else {
                            //不存在scope注解表示当前类是单例
                            beanDefinition.setScope(SINGLETON_BEAN);
                        }
                        beanDefinitionMap.put(beanName,beanDefinition);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Object getBean(String beanName) {
        if (beanDefinitionMap.containsKey(beanName)) {
            //根据字符串，找到类
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals(SINGLETON_BEAN)) {
                return singleObjects.get(beanName);
            }else{
                //不是单例则创建一个对象
            }
        }else{
            //没有则抛出异常
        }
        return null;
    }
}
