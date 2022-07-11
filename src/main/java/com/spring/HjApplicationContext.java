package com.spring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class HjApplicationContext {

    private Class configClass;

    private ConcurrentHashMap<String,Object> singletonObjects = new ConcurrentHashMap<>();//单例池
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(); //存整个系统一开始扫描到的所有Bean和它的的定义
    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    //构造一个spring容器，传入配置类
    public HjApplicationContext(Class configClass) {
        this.configClass = configClass;

        //解析拿到的配置类
        //解析ComponentScan注解相关信息-->拿到扫描路径-->spring去扫描拿到注解信息，解析注解-->生成beanDefinition-->放到map中
        scan(configClass);

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(beanName,beanDefinition);//创建得到单例bean对象
                singletonObjects.put(beanName, bean);
            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();

            //此时实例化出来的对象，就需要spring来对这个对象内部的属性进行赋值-依赖注入
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    Object bean = getBean(declaredField.getName());
                    declaredField.setAccessible(true);
                    declaredField.set(instance, bean);//spring给实例化属性赋值的前提，是需要从容器中获取到这个对象
                }
            }

            //Aware回调
            //判断当前这个实例化对象是不是实现了这个接口
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessorBeforeInitialization(instance, beanName);
            }

            //初始化
            if (instance instanceof InitializingBean) {
                try {
                    ((InitializingBean) instance).afterPropertiesSet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessorAfterInitialization(instance, beanName);
            }



            return instance;
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

        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);//拿到传入配置类中的相关注解

        //拿到注解后获取注解中的属性
        String pathValue = componentScanAnnotation.value();
        pathValue = pathValue.replace(".","/");
        //扫描
        //Bootstrap--->jre/lib
        //Ext--->jre/ext/lib
        //App--->classpath
        ClassLoader classLoader = HjApplicationContext.class.getClassLoader();//获得类加载器
        //URL resource = classLoader.getResource("com/hj/service");//获得一个目录或者文件
        URL resource = classLoader.getResource(pathValue);
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                //F:\git\hj_spring\target\classes\com\hj\service\UserService.class
                //将上述字符串的前一部分去掉，尾部.class去掉，中间的斜杠换成. 就可以
                String absolutePath = f.getAbsolutePath();
                //再判断是不是类文件，然后再进行处理
                if (absolutePath.endsWith(".class")) {
                    String className = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
                    className = className.replace("\\", ".");
                    //通过类加载器获得一个class对象
                    //再判断这个class对象上面是不是有Component注解
                    //Class<?> aClass = classLoader.loadClass("com.hj.service.UserService");//此处写的是想要加载的类的名，而不是路径
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (clazz.isAnnotationPresent(Component.class)) {

                            //BeanPostProcessor
                            //判断当前clazz对象是否实现BeanPostProcessor接口的方法如下
                            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                BeanPostProcessor instance = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                                beanPostProcessorList.add(instance);
                            }


                            //如果有Component注解,表示当前类是一个Bean
                            //如果当前类是一个Bean，就需要创建这个Bean对象么?-首先需要判断当前这个Bean是单例还是原型
                            //判断依据就是根据BeanDefinition(在解析过程中创建的)
                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);//拿到component注解的名字
                            String beanNameValue = componentAnnotation.value();//当前Bean的名字
                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(clazz);
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                //如果存在Scope注解则去拿这个注解
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scopeAnnotation.value());//把当前用户设置的值设置到beanDefinition中
                            } else {
                                //如果不存在则说明当前这个Bean是一个单例
                                beanDefinition.setScope("singleton");
                            }
                            beanDefinitionMap.put(beanNameValue, beanDefinition);

                        }
                    } catch (ClassNotFoundException | NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Object getBean(String beanName) {
        //判断map中是否含有当前这个bean
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                //如果当前bean是一个单例bean，则从单例池中获取这个bean对象然后返回这个对象
                Object o = singletonObjects.get(beanName);
                return o;
            } else {
                //反之是一个原型bean，则创建bean对象
                Object bean = createBean(beanName,beanDefinition);
                return bean;
            }
        } else {
            throw new NullPointerException();//抛出异常，不存在这个bean
        }
    }
}
