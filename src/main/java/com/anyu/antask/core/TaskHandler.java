package com.anyu.antask.core;

import com.anyu.antask.common.Result;
import com.anyu.antask.common.Task;
import com.anyu.antask.common.TaskStatus;
import com.anyu.antask.handler.AfterAdvise;
import com.anyu.antask.handler.DataSourceConsumer;
import com.anyu.antask.handler.DataSourceHandler;
import com.anyu.antask.handler.DataSourceSupplier;
import com.anyu.antask.util.CollUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

/**
 * 任务处理实现
 *
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/21
 */
@Getter
@Log4j2
public class TaskHandler<T, R> {
    private final String taskName;
    private final TaskHelper taskHelper;
    private final DataSourceSupplier<T> dataSupplier;
    private final DataSourceConsumer<R> dataConsumer;
    private final DataSourceHandler<T, R> dataHandler;
    private final Task task;
    private Predicate<Task> beforeHandler;
    private AfterAdvise afterAdvise;

    public TaskHandler(String taskName, TaskHelper taskHelper
            , DataSourceSupplier<T> dataSupplier
            , DataSourceConsumer<R> dataConsumer
            , DataSourceHandler<T, R> dataHandler) {
        if (taskName == null || taskHelper == null
                || dataConsumer == null || dataSupplier == null || dataHandler == null)
            throw new IllegalArgumentException("参数不能为空");
        this.taskName = taskName;
        this.taskHelper = taskHelper;
        this.dataSupplier = dataSupplier;
        this.dataConsumer = dataConsumer;
        this.dataHandler = dataHandler;
        this.task = taskHelper.getTaskByName(taskName)
                .orElseThrow(() -> new RuntimeException("task name 数据库中不存在"));
    }

    /**
     * 设置后置通知
     *
     * @param afterAdvise 后置通知
     */
    public void setAfterAdvise(AfterAdvise afterAdvise) {
        this.afterAdvise = afterAdvise;
    }

    /**
     * 设置前置通知
     *
     * @param beforeHandler 前置通知
     */
    public void setBeforeAdvise(Predicate<Task> beforeHandler) {
        this.beforeHandler = beforeHandler;
    }

    /**
     * 任务处理
     */
    @SuppressWarnings("unchecked")
    public void handle() {
        if (beforeHandler != null && !beforeHandler.test(task)) {
            log.info("Task ==> 任务 {} 执行条件不满足，停止执行", taskName);
            return;
        }
        //任务开始
        final Result result = new Result();
        while (true) {
            final List<T> ts = dataSupplier.supplier();
            if (CollUtil.isEmpty(ts))
                break;
            result.addSTotal(ts.size());
            List<R> rs;
            if (dataHandler == null) {
                rs = (List<R>) ts;
            } else {
                rs = dataHandler.handle(result, ts);
            }
            if (CollUtil.isNotEmpty(rs))
                dataConsumer.consumer(rs, result);
        }
        if (afterAdvise != null) {
            afterAdvise.after(result);
        }
        //任务更新
        taskHelper.updateTaskLastExcTime(taskName, LocalDateTime.now());
        changeTaskStatus(TaskStatus.DONE);
    }

    /**
     * 更改任务状态
     *
     * @param taskStatus 任务状态
     */
    public void changeTaskStatus(TaskStatus taskStatus) {
        taskHelper.updateTaskStatus(taskName, taskStatus);
    }

    /**
     * 是否是不合法的任务执行状态
     *
     * @return true：是; false: 否
     */
    public boolean isNotLegalExecuteTaskStatus() {
        // TODO 执行中，等待执行中，执行时间是不合法
        return false;
    }
}
