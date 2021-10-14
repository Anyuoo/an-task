package com.anyu.antask.handler;


import com.anyu.antask.common.Result;

import java.util.function.Consumer;

/**
 * 后置痛着
 *
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/21
 */
public class AfterAdvise {
    private final Consumer<Result> adviseHandler;

    public AfterAdvise(Consumer<Result> adviseHandler) {
        this.adviseHandler = adviseHandler;
    }

    public void after(Result result) {
        adviseHandler.accept(result);
    }
}
