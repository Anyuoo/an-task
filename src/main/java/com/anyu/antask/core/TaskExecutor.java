package com.anyu.antask.core;

import com.anyu.antask.common.TaskStatus;
import com.anyu.antask.util.CollUtil;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务中心
 *
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/21
 */
@Log4j2
public class TaskExecutor implements TaskExecutorService {

    private final ConcurrentHashMap<String, TaskHandler<?, ?>> taskHandlerMap;
    private final AtomicInteger runningTaskNum = new AtomicInteger(0);
    private int taskHandlerMapCap = 16;
    private ThreadPoolExecutor executor;

    public TaskExecutor() {
        this.taskHandlerMap = new ConcurrentHashMap<>(taskHandlerMapCap);
    }

    public TaskExecutor(int taskHandlerMapCap, ThreadPoolExecutor executor) {
        this.taskHandlerMapCap = taskHandlerMapCap;
        this.executor = executor;
        this.taskHandlerMap = new ConcurrentHashMap<>(taskHandlerMapCap);
    }

    @Override
    public void execute() {
        if (taskHandlerMap.isEmpty())
            return;
        if (executor == null)
            executor = initThreadPoolExecutor();

        for (Map.Entry<String, TaskHandler<?, ?>> entry : taskHandlerMap.entrySet()) {
            final TaskHandler<?, ?> taskHandler = entry.getValue();
            executor.execute(() -> {
                log.info("Task ==> 任务 {} 开始执行", taskHandler.getTaskName());
                try {
                    taskHandler.changeTaskStatus(TaskStatus.RUNNING);
                    taskHandler.handle();
                    taskHandler.changeTaskStatus(TaskStatus.DONE);
                } catch (Exception e) {
                    taskHandler.changeTaskStatus(TaskStatus.ERROR);
                    log.error("Task ==> 任务 {} 执行失败, 异常信息: {}", taskHandler.getTaskName(), e.getLocalizedMessage());
                }
                runningTaskNum.decrementAndGet();
            });
            taskHandlerMap.remove(taskHandler.getTaskName());
            runningTaskNum.incrementAndGet();
        }
    }

    @Override
    public boolean addTaskHandler(TaskHandler<?, ?> taskHandler) {
        if (taskHandler == null)
            throw new IllegalArgumentException("Task ==> 添加任务处理失败");
        if (taskHandler.isNotLegalExecuteTaskStatus())
            throw new RuntimeException("Task ==> 任务不满足执行条件");
        if (taskHandlerMap.containsKey(taskHandler.getTaskName()))
            return false;
        taskHandlerMap.put(taskHandler.getTaskName(), taskHandler);
        taskHandler.changeTaskStatus(TaskStatus.READY);
        return true;

    }

    @Override
    public List<String> addTaskHandlers(List<TaskHandler<?, ?>> taskHandlers) {
        if (CollUtil.isEmpty(taskHandlers))
            throw new IllegalArgumentException("Task ==> 批量添加任务处理失败");
        final List<String> taskNames = new ArrayList<>();
        for (TaskHandler<?, ?> taskHandler : taskHandlers) {
            final String taskName = taskHandler.getTaskName();
            taskHandlerMap.put(taskName, taskHandler);
            taskNames.add(taskName);
        }
        return taskNames;
    }

    @Override
    public TaskHandler<?, ?> removeTaskHandler(String taskName) {
        final TaskHandler<?, ?> taskHandler = taskHandlerMap.remove(taskName);
        if (taskHandler == null)
            throw new RuntimeException("Task =>> 移除任务处理失败");
        taskHandler.changeTaskStatus(TaskStatus.NONE);
        return taskHandler;
    }

    @Override
    public void destroy() {
        if (executor != null)
            executor.shutdown();
    }

    /**
     * 初始化线程池
     *
     * @return 线程池
     */
    private ThreadPoolExecutor initThreadPoolExecutor() {
        return new ThreadPoolExecutor(0, 20
                , 5, TimeUnit.MINUTES
                , new LinkedBlockingDeque<>(5)
                , new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
