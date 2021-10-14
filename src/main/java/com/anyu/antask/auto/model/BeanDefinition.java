package com.anyu.antask.auto.model;

import java.lang.reflect.Method;

/**
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/26 15:21
 */
public class BeanDefinition {
    private final String beanName;
    private final Object bean;
    private Class<?> conClazz;
    private Method consumer;
    private Class<?> supClazz;
    private Method supplier;
    private Method handler;
    private Method cursor;

    public BeanDefinition(String beanName, Object bean) {
        this.beanName = beanName;
        this.bean = bean;
    }

    public String getBeanName() {
        return beanName;
    }

    public Object getBean() {
        return bean;
    }

    public Class<?> getConClazz() {
        if (conClazz == null)
            throw new RuntimeException(beanName + "未找到消费类型");
        return conClazz;
    }

    public void setConClazz(Class<?> conClazz) {
        if (this.conClazz == null)
            this.conClazz = conClazz;
        if (!this.conClazz.equals(conClazz))
            throw new IllegalArgumentException(beanName + "消费参数类型不匹配");
    }

    public Method getConsumer() {
        if (consumer == null)
            throw new RuntimeException(beanName + "未找到消费函数");
        return consumer;
    }

    public void setConsumer(Method consumer) {
        if (this.consumer == null)
            this.consumer = consumer;
        if (!this.consumer.equals(consumer))
            throw new IllegalArgumentException(beanName + "存在多个消费函数");
    }

    public Class<?> getSupClazz() {
        if (supClazz == null)
            throw new RuntimeException(beanName + "未找到提供类型");
        return supClazz;
    }

    public void setSupClazz(Class<?> supClazz) {
        if (this.supClazz == null)
            this.supClazz = supClazz;
        if (!this.supClazz.equals(supClazz))
            throw new IllegalArgumentException(beanName + "提供函数参数不匹配");
    }

    public Method getSupplier() {
        if (supplier == null)
            throw new RuntimeException(beanName + "未找到提供函数");
        return supplier;
    }

    public void setSupplier(Method supplier) {
        if (this.supplier == null)
            this.supplier = supplier;
        if (!this.supplier.equals(supplier))
            throw new IllegalArgumentException(beanName + "存在多个消费函数");
    }

    public Method getHandler() {
        return handler;
    }

    public void setHandler(Method handler) {
        this.handler = handler;
    }

    public Method getCursor() {
        return cursor;
    }

    public void setCursor(Method cursor) {
        this.cursor = cursor;
    }
}
