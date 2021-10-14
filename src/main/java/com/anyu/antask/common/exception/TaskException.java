package com.anyu.antask.common.exception;

import lombok.Getter;

/**
 * 系统异常
 * @author Anyu
 * @version 1.0.0
 * @since 2021/9/12
 */
@Getter
public class TaskException extends RuntimeException {
    private final int code;
    private final String msg;
    private final TaskETypeFunc taskETypeFunc;

    public TaskException(TaskETypeFunc taskETypeFunc, Throwable cause){
        this(taskETypeFunc, taskETypeFunc.getMsg(), cause);
    }

    public TaskException(TaskETypeFunc eTypeFunc, String extMsg, Throwable cause) {
        super(extMsg, cause);
        this.msg = extMsg;
        this.code = eTypeFunc.getCode();
        this.taskETypeFunc = eTypeFunc;
    }

    public TaskException(TaskETypeFunc taskETypeFunc) {
        this(taskETypeFunc, null);
    }

    public static TaskException causeBy(TaskETypeFunc eTypeFunc, String extMsg) {
        return new TaskException(eTypeFunc, extMsg, null);
    }

    public static TaskException causeBy(TaskETypeFunc eTypeFunc) {
        return new TaskException(eTypeFunc);
    }
}
