package com.anyu.antask.core;

import com.anyu.antask.common.Task;
import com.anyu.antask.common.TaskStatus;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 任务数据库交互
 *
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/21
 */
public interface TaskHelper {

    /**
     * 获取任务信息
     *
     * @param taskName 任务名
     * @return 任务信息
     */
    Optional<Task> getTaskByName(String taskName);

    /**
     * 通过任务名比较两个任务的最后执行时间
     *
     * @param taskName0 任务1
     * @param taskName1 任务2
     * @return 相差时间天数
     */
    int compareTaskLastExcTime(String taskName0, String taskName1);

    /**
     * 通过任务名更改最后一次之心时间
     *
     * @param taskName 任务名
     * @param excTime  执行时间
     */
    void updateTaskLastExcTime(String taskName, LocalDateTime excTime);

    /**
     * 更改任务状态
     *
     * @param taskName   任务名
     * @param taskStatus 任务状态
     */
    void updateTaskStatus(String taskName, TaskStatus taskStatus);
}
