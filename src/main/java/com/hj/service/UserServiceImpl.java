package com.hj.service;

import com.spring.Autowired;
import com.spring.BeanNameAware;
import com.spring.Component;
import com.spring.InitializingBean;

@Component("userService")
//@Scope("prototype")//不加该注解，表示当前类只是一个单例Bean，反之加上该注解表示当前类是一个原型Bean
public class UserServiceImpl implements UserService{
    @Autowired
    private OrderService orderService;

    //private String beanName;//想要给beanName赋值为userService的bean名字，就需要用到BeanNameAware接口

//    @Override
//    public void setBeanName(String name) {
//        beanName = name;
//    }
    private String name;

    public void setName(String name) {
        this.name = name;
    }

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        System.out.println("初始化");
//    }

    public void test() {
        System.out.println(orderService);
        System.out.println(name);
    }


}
