package com.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)//表示只能写在类上面
public @interface ComponentScan {
    String value() default "";//该注解是定义扫描路径的，所以此处定义一个属性来
}
