package com.anyu.antask.core;

import lombok.extern.log4j.Log4j2;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 任务中心定时任务
 *
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/22
 */
@Log4j2
public class TaskExecutorSchedule {
    private final TaskExecutorService taskExecutor;
    private final long periodSec;
    private final long delaySec;

    public TaskExecutorSchedule(TaskExecutorService taskExecutor, long delaySec, long periodSec) {
        this.taskExecutor = taskExecutor;
        this.periodSec = periodSec;
        this.delaySec = delaySec;
    }

    public void start() {
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                taskExecutor.execute();
            }
        };
        new Timer().schedule(timerTask, delaySec * 1000, periodSec * 1000);
        log.info("任务中心开始定时执行任务, 每 {} 秒执行一次", periodSec);
    }

}
