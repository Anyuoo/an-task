package com.anyu.antask.common.exception;

import lombok.AllArgsConstructor;

/**
 * 异常类型
 * @author Anyu
 * @version 1.0.0
 * @since 2021/9/12
 */
@AllArgsConstructor
public enum TaskEType implements TaskETypeFunc {
    PARAMETER_ERROR(1000, "参数错误"),
    ;

    private final int code;
    private final String msg;


    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
