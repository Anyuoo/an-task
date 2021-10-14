package com.anyu.antask.auto;

import com.anyu.antask.auto.model.BeanDefinition;
import com.anyu.antask.core.TaskExecutor;
import com.anyu.antask.core.TaskExecutorSchedule;
import com.anyu.antask.core.TaskHandler;
import com.anyu.antask.core.TaskHelper;


import java.util.List;
import java.util.Map;

/**
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/25
 */
public class TaskHandlerAutoConfig {

    private final TaskHelper taskHelper;

    public TaskHandlerAutoConfig(TaskHelper taskHelper) {
        this.taskHelper = taskHelper;
    }

    public void autoInit() {
        final SourceServiceScanner serviceScanner = new SourceServiceScanner();
        final List<Class<?>> serviceClasses = serviceScanner.scanSourceServiceClassOfBasePackage();
        final ServiceBeanAnalyser serviceBeanAnalyser = new ServiceBeanAnalyser(serviceClasses);
        final Map<String, BeanDefinition> beanDefinitionMap = serviceBeanAnalyser.getServiceBeanDefinitionMap();
        final TaskHandlerBeanBuilder taskHandlerBeanBuilder = new TaskHandlerBeanBuilder(taskHelper, beanDefinitionMap);
        final Map<String, TaskHandler<?, ?>> taskHandlerMap = taskHandlerBeanBuilder.buildTaskHandlers();
        final TaskExecutor taskExecutor = new TaskExecutor();
        final TaskContext taskContext = new TaskContext(taskExecutor);
        taskContext.addAllTasks(taskHandlerMap.values());
        final TaskExecutorSchedule executorSchedule = new TaskExecutorSchedule(taskExecutor, 0, 5);
        executorSchedule.start();
        taskContext.runTask("USER_INFO_TASK");
    }
}
