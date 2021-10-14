package com.anyu.antask.handler;

import com.anyu.antask.common.Result;
import com.anyu.antask.util.CollUtil;


import java.util.List;
import java.util.function.BiConsumer;

/**
 * 数据消费者
 *
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/21
 */
public class DataSourceConsumer<R> {
    private final BiConsumer<List<R>, Result> saveHandler;

    public DataSourceConsumer(BiConsumer<List<R>, Result> saveHandler) {
        this.saveHandler = saveHandler;
    }

    public void consumer(List<R> rs, Result result) {
        if (CollUtil.isEmpty(rs))
            return;
        saveHandler.accept(rs, result);
        result.addCTotal(rs.size());
    }
}
