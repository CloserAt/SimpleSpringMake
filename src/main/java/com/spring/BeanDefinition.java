package com.spring;

public class BeanDefinition {
    private Class clazz;//当前Bean的类型
    private String scope;//当前Bean的作用域

//    public BeanDefinition(Class clazz, String scope) {
//        this.clazz = clazz;
//        this.scope = scope;
//    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
