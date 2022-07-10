package com.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)//表示只能写在类上面
public @interface Scope {
    String value() default "";//定义bean的名字，一般不传入bean名的话spring会自动扫描类名解析成bean name
}
