package com.anyu.antask.auto;

import com.anyu.antask.auto.model.BeanDefinition;
import com.anyu.antask.common.stereotype.SourceConsumer;
import com.anyu.antask.common.stereotype.SourceHandler;
import com.anyu.antask.common.stereotype.SourceService;
import com.anyu.antask.common.stereotype.SourceSupplier;
import com.anyu.antask.core.TaskHandler;
import com.anyu.antask.util.TaskSpringContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/26 15:24
 */
public class ServiceBeanAnalyser {

    private final Map<String, BeanDefinition> beanDefinitionMap;

    private final List<Class<?>> serviceClasses;

    public ServiceBeanAnalyser(List<Class<?>> serviceClasses) {
        beanDefinitionMap = new HashMap<>();
        this.serviceClasses = serviceClasses;
    }

    /**
     * 分析传入的 {@link SourceService}标识的类信息
     * 并从Spring IOC 获取实例对象
     * 解析实例对象封装成 {@link BeanDefinition}
     *
     * @return {@link TaskHandler}定义的访法信息
     */
    public Map<String, BeanDefinition> getServiceBeanDefinitionMap() {
        for (Class<?> serviceClass : serviceClasses) {
            final Object serviceBean = TaskSpringContext.getBean(serviceClass);
            analyseServiceBean(serviceBean, serviceClass);
        }
        return beanDefinitionMap;
    }

    /**
     * 分析 {@link SourceService}标识的 bean
     * 并将其拥有的方法进行分类收集到 {@link BeanDefinition}中
     *
     * @param bean {@link SourceService}标识的 bean
     */
    private void analyseServiceBean(Object bean, Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            final SourceSupplier supplier = method.getDeclaredAnnotation(SourceSupplier.class);
            if (supplier != null) {
                final String name = supplier.name();
                BeanDefinition bd;
                if (beanDefinitionMap.containsKey(name))
                    bd = beanDefinitionMap.get(name);
                else {
                    bd = new BeanDefinition(name, bean);
                    beanDefinitionMap.put(name, bd);
                }
                bd.setSupplier(method);
                bd.setSupClazz(supplier.supClass());
            }
            final SourceConsumer consumer = method.getDeclaredAnnotation(SourceConsumer.class);
            if (consumer != null) {
                final String name = consumer.name();
                BeanDefinition bd;
                if (beanDefinitionMap.containsKey(name))
                    bd = beanDefinitionMap.get(name);
                else {
                    bd = new BeanDefinition(name, bean);
                    beanDefinitionMap.put(name, bd);
                }
                bd.setConsumer(method);
                bd.setConClazz(consumer.conClass());
            }
            final SourceHandler handler = method.getDeclaredAnnotation(SourceHandler.class);
            if (handler != null) {
                final String name = handler.name();
                BeanDefinition bd;
                if (beanDefinitionMap.containsKey(name))
                    bd = beanDefinitionMap.get(name);
                else {
                    bd = new BeanDefinition(name, bean);
                    beanDefinitionMap.put(name, bd);
                }
                bd.setHandler(method);
                bd.setSupClazz(handler.supClass());
                bd.setConClazz(handler.conClass());
            }
        }
    }


}
