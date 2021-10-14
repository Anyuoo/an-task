package com.anyu.antask.handler;

import com.anyu.antask.common.Result;
import com.anyu.antask.util.CollUtil;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/23
 */
@Getter
public class DataSourceHandler<T, R> {

    private final BiFunction<List<T>, Result, List<R>> handler;

    public DataSourceHandler(BiFunction<List<T>, Result, List<R>> handler) {
        this.handler = handler;
    }

    public List<R> handle(Result result, List<T> ts) {
        if (CollUtil.isEmpty(ts))
            return Collections.emptyList();
        final List<R> rs = handler.apply(ts, result);
        if (CollUtil.isEmpty(rs))
            return Collections.emptyList();
        result.addFTotal(ts.size() - rs.size());
        return rs;
    }

}
