package com.anyu.antask.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 访问 spring 上下文
 *
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/26
 */
public class TaskSpringContext implements ApplicationContextAware {

    private static TaskSpringContext instance;
    private ApplicationContext context;

    public TaskSpringContext() {
        instance = this; //赋值给静态对象
    }

    //提供静态访问
    public static <T> T getBean(Class<T> clazz) {
        return instance.context.getBean(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
