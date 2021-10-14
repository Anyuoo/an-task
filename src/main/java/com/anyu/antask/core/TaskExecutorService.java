package com.anyu.antask.core;

import java.util.List;

/**
 * 任务中心服务
 *
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/21
 */
public interface TaskExecutorService {

    /**
     * 添加任务处理器
     *
     * @param taskHandler 任务处理器
     */
    boolean addTaskHandler(TaskHandler<?, ?> taskHandler);

    /**
     * 添加任务处理器
     *
     * @param taskHandlers 任务处理器
     * @return 成功任务名
     */
    List<String> addTaskHandlers(List<TaskHandler<?, ?>> taskHandlers);

    /**
     * 任务中心执行
     */
    void execute();

    /**
     * 移除任务处理器
     *
     * @param taskName 任务名
     * @return 任务处理器
     */
    TaskHandler<?, ?> removeTaskHandler(String taskName);

    /**
     * 注销任务中心资源
     */
    void destroy();
}
