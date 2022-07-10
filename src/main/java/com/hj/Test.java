package com.hj;

import com.spring.HjApplicationContext;

public class Test {
    public static void main(String[] args) {
        HjApplicationContext hjApplicationContext = new HjApplicationContext(AppConfig.class);//此处的传入参数是当前用户需要向spring传入的配置文件

        /*
        单例Bean和原型Bean的区别：
            单例Bean:多次调用getBean()方法，返回的是同一个对象
            原型Bean:多次调用getBean()方法，返回的是多个不同的对象
        实现原理：
            单例Bean实现原理：底层使用map<beanName,bean对象> 同一个beanName获得同一个bean对象 -即单例池
         */
        Object userService = hjApplicationContext.getBean("userService");//通过类拿到bean的名字
//        System.out.println(userService);
//        Object userService1 = hjApplicationContext.getBean("userService");
//        System.out.println(userService1); 
    }
}
