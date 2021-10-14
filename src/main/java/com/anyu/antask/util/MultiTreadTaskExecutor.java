package com.anyu.antask.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * 多线程任务执行器
 *
 * @author Anyu
 * @version 1.0.0
 * @since 2021/9/11
 */
@Log4j2
public class MultiTreadTaskExecutor<T> {
    private static final int DEFAULT_MIN_TASK_LIMIT = 10;
    private static final int DEFAULT_TASK_EXEC_SIZE = 3;
    /**
     * 任务列表
     */
    private final List<T> tasks;
    /**
     * 一个线程执行的任务数
     */
    private final int batchTaskExecSize;
    /**
     * 开启多线程任务的最小任务数
     */
    private final int minTaskLimit;
    private ThreadPoolTaskExecutor taskExecutor;

    public MultiTreadTaskExecutor(List<T> tasks) {
        this(tasks, null);
    }

    public MultiTreadTaskExecutor(List<T> tasks, int batchTaskExecSize, int minTaskLimit) {
        this(tasks, null, batchTaskExecSize, minTaskLimit);
    }

    public MultiTreadTaskExecutor(List<T> tasks, ThreadPoolTaskExecutor taskExecutor) {
        this(tasks, taskExecutor, DEFAULT_TASK_EXEC_SIZE, DEFAULT_MIN_TASK_LIMIT);
    }

    public MultiTreadTaskExecutor(List<T> tasks, ThreadPoolTaskExecutor taskExecutor, int batchTaskExecSize, int minTaskLimit) {
        this.tasks = tasks;
        this.batchTaskExecSize = Math.max(batchTaskExecSize, DEFAULT_TASK_EXEC_SIZE);
        this.taskExecutor = taskExecutor;
        this.minTaskLimit = Math.max(minTaskLimit, DEFAULT_MIN_TASK_LIMIT);
    }

    public void execute(Consumer<T> consumer) {
        if (consumer == null)
            throw new IllegalArgumentException("执行参数consumer不能为null");
        if (CollUtil.isEmpty(tasks))
            return;
        if (tasks.size() < minTaskLimit) {
            tasks.forEach(consumer);
            return;
        }
        final int count = tasks.size() / batchTaskExecSize;
        final int latchNum = tasks.size() % batchTaskExecSize == 0 ? count : count + 1;
        final CountDownLatch countDownLatch = new CountDownLatch(latchNum);
        if (taskExecutor == null)
            taskExecutor = buildTaskExecutor();
        for (List<T> partTasks : CollUtil.split(tasks, batchTaskExecSize)) {
            taskExecutor.execute(() -> {
                try {
                    partTasks.forEach(consumer);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.warn("任务执行失败，异常信息：{}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }


    private ThreadPoolTaskExecutor buildTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(12);
        executor.setQueueCapacity(100);
        // 配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("multiTreadTaskExecutor");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        // 执行初始化
        executor.initialize();
        return executor;
    }

}
