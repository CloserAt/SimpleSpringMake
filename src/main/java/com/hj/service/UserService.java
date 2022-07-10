package com.hj.service;

import com.spring.Component;
import com.spring.Scope;

@Component("userService")
//@Scope("prototype")//不加该注解，表示当前类只是一个单例Bean，反之加上该注解表示当前类是一个原型Bean
public class UserService {
}
