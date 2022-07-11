package com.hj.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class beanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessorBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化前");
        if (beanName.equals("userService")) {
            ((UserServiceImpl)bean).setName("hello world");
        }
        return bean;
    }

    @Override
    public Object postProcessorAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后");
        //userService即bean到底要不要aop，是跟据bean的对应的切点前面是否匹配
        if (beanName.equals("userService")) {
            Object proxyInstance = Proxy.newProxyInstance(beanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("代理逻辑"); //找切点，执行切点的对应的方法
                    return method.invoke(bean, args);
                }
            });
            return proxyInstance;
        }
        return bean;
    }
}
