package com.anyu.antask.auto;

import com.anyu.antask.auto.model.BeanDefinition;
import com.anyu.antask.common.stereotype.SourceCursor;
import com.anyu.antask.core.TaskHandler;
import com.anyu.antask.core.TaskHelper;
import com.anyu.antask.handler.DataSourceConsumer;
import com.anyu.antask.handler.DataSourceHandler;
import com.anyu.antask.handler.DataSourceSupplier;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过 {@link BeanDefinition}信息构建{@link TaskHandler}
 *
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/26 15:53
 */

public class TaskHandlerBeanBuilder {

    private final TaskHelper taskHelper;

    private final Map<String, BeanDefinition> beanDefinitionMap;

    public TaskHandlerBeanBuilder(TaskHelper taskHelper, Map<String, BeanDefinition> beanDefinitionMap) {
        this.taskHelper = taskHelper;
        this.beanDefinitionMap = beanDefinitionMap;
    }

    public Map<String, TaskHandler<?, ?>> buildTaskHandlers() {
        if (beanDefinitionMap == null || beanDefinitionMap.isEmpty())
            return new HashMap<>();
        final HashMap<String, TaskHandler<?, ?>> taskHandlerMap = new HashMap<>();
        beanDefinitionMap.forEach((name, bd) -> taskHandlerMap.put(name, buildTaskBean(bd)));
        return taskHandlerMap;
    }

    private TaskHandler<?, ?> buildTaskBean(BeanDefinition bd) {
        final DataSourceSupplier<?> sourceSupplier = buildSourceSupplier(bd);

        final DataSourceConsumer<?> sourceConsumer = buildSourceConsumer(bd);

        final DataSourceHandler<?, ?> sourceHandler = buildSourceHandler(bd);
        return new TaskHandler(bd.getBeanName(), taskHelper
                , sourceSupplier, sourceConsumer, sourceHandler);
    }

    private DataSourceHandler<?, ?> buildSourceHandler(BeanDefinition bd) {
        return new DataSourceHandler<>((rs, result) -> {
            final Method handler = bd.getHandler();
            if (handler != null) {
                try {
                    return (List<?>) handler.invoke(bd.getBean(), rs, result);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    private DataSourceSupplier<?> buildSourceSupplier(BeanDefinition bd) {
        final Method cursorFunc = parseSupplerEntityCursor(bd.getSupClazz());
        final Method supplier = bd.getSupplier();
        if (cursorFunc == null) {
            return new DataSourceSupplier<>(() -> {
                try {
                    return (List<?>) supplier.invoke(bd.getBean());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return null;
            });
        }
        return new DataSourceSupplier<>((supEntity) -> {
            try {
                return cursorFunc.invoke(supEntity);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }, (lastCur, size) -> {
            try {
                return (List<?>) supplier.invoke(bd.getBean(), lastCur, size);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    private DataSourceConsumer<?> buildSourceConsumer(BeanDefinition bd) {
        return new DataSourceConsumer<>((ts, result) -> {
            final Method consumer = bd.getConsumer();
            try {
                consumer.invoke(bd.getBean(), ts, result);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 解析数据提供实体游标函数游标
     *
     * @param supClass 提供实体类
     * @return 实体的油表字段函数
     */
    private Method parseSupplerEntityCursor(Class<?> supClass) {
        final Field[] fields = supClass.getDeclaredFields();
        Method cursorFunc = null;
        for (Field field : fields) {
            final SourceCursor cursor = field.getDeclaredAnnotation(SourceCursor.class);
            if (cursor == null) continue;
            final String name = field.getName();
            try {
                cursorFunc = supClass.getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return cursorFunc;
    }
}
