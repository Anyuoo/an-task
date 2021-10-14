package com.anyu.antask.common;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/22
 */
@Getter
@AllArgsConstructor
public enum TaskStatus {
    NONE(0, "任务不存在"),
    READY(1, "任务就绪"),
    RUNNING(2, "任务正在执行"),
    ERROR(3, "任务执行错误"),
    DONE(4, "任务执行完成"),
    ;
    @EnumValue
    private final int code;
    private final String msg;
}
