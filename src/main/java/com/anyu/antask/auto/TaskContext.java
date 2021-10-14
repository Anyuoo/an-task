package com.anyu.antask.auto;

import com.anyu.antask.common.TaskStatus;
import com.anyu.antask.core.TaskExecutorService;
import com.anyu.antask.core.TaskHandler;
import com.anyu.antask.util.CollUtil;


import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 任务容器
 *
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/26
 */
public class TaskContext {
    //正在运行的任务
    private final ConcurrentHashMap<String, TaskHandler<?, ?>> runningMap;
    //所有的任务
    private final ConcurrentHashMap<String, TaskHandler<?, ?>> mainMap;

    private final TaskExecutorService taskExecutor;

    public TaskContext(TaskExecutorService taskExecutor) {
        if (taskExecutor == null)
            throw new IllegalArgumentException("TaskExecutorService 不能为空");
        this.taskExecutor = taskExecutor;
        mainMap = new ConcurrentHashMap<>();
        runningMap = new ConcurrentHashMap<>();
    }

    public synchronized int runTasks(List<String> taskNames) {
        if (CollUtil.isEmpty(taskNames))
            throw new IllegalArgumentException("taskNames 不能为空");
        chekRunningTaskHandlerStatus();
        //过滤正在运行的任务
        for (String runningName : runningMap.keySet())
            taskNames.remove(runningName);
        final List<TaskHandler<?, ?>> handlers = taskNames.stream()
                .map(mainMap::get)
                .collect(Collectors.toList());
        final List<String> sucNames = taskExecutor.addTaskHandlers(handlers);
        if (CollUtil.isNotEmpty(sucNames)) {
            final List<TaskHandler<?, ?>> sucTaskHandlers = sucNames.stream()
                    .map(mainMap::get)
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(sucTaskHandlers))
                throw new RuntimeException("不合法任务，未能从任务列表中发现");
            for (TaskHandler<?, ?> sucTaskHandler : sucTaskHandlers)
                runningMap.put(sucTaskHandler.getTaskName(), sucTaskHandler);
        }
        return sucNames.size();
    }

    public synchronized void runTask(String taskName) {
        if (taskName == null)
            throw new IllegalArgumentException("taskName 参数不能为空");
        chekRunningTaskHandlerStatus();
        if (runningMap.containsKey(taskName))
            return;
        final TaskHandler<?, ?> taskHandler = mainMap.get(taskName);
        if (taskExecutor.addTaskHandler(taskHandler)) {
            runningMap.put(taskName, taskHandler);
            return;
        }
        throw new RuntimeException("添加任务失败");
    }

    public void addAllTasks(Collection<TaskHandler<?, ?>> taskHandlers) {
        if (CollUtil.isNotEmpty(taskHandlers))
            taskHandlers.forEach(taskHandler -> mainMap.put(taskHandler.getTaskName(), taskHandler));
    }


    public void chekRunningTaskHandlerStatus() {
        for (String taskName : runningMap.keySet()) {
            final TaskStatus taskStatus = runningMap.get(taskName).getTask().getTaskStatus();
            if (taskStatus != TaskStatus.RUNNING)
                runningMap.remove(taskName);
        }
    }

    public TaskHandler<?, ?> getTaskHandler(String taskName) {
        return mainMap.get(taskName);
    }

    public TaskHandler<?, ?> getRunningTaskHandler(String taskName) {
        return runningMap.get(taskName);
    }

    public int runningSize() {
        return runningMap.size();
    }

    public int taskSize() {
        return mainMap.size();
    }

}
