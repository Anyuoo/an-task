package com.anyu.antask.handler;

import com.anyu.antask.util.CollUtil;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 数据提供者
 *
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/21
 */
@Getter
public class DataSourceSupplier<T> {
    private Integer size = 1000;
    private String lastCursor;
    private Function<T, ?> cursor;
    private BiFunction<String, Integer, List<T>> queryParamHandler;
    private Supplier<List<T>> queryHandler;
    private boolean hasData;

    public DataSourceSupplier(Function<T, ?> cursor, BiFunction<String, Integer, List<T>> queryParamHandler) {
        if (cursor == null || queryParamHandler == null)
            throw new NullPointerException("数据提供参数不合法");
        this.hasData = true;
        this.cursor = cursor;
        this.queryParamHandler = queryParamHandler;
    }

    /**
     * 分页提供
     *
     * @param size              页大小
     * @param cursor            分页游标
     * @param queryParamHandler 数据提供函数
     */
    public DataSourceSupplier(Integer size, Function<T, ?> cursor, BiFunction<String, Integer, List<T>> queryParamHandler) {
        this(cursor, queryParamHandler);
        this.size = size;
        this.queryParamHandler = queryParamHandler;
    }

    /**
     * 提供所有数据
     *
     * @param queryHandler 数据提供函数
     */
    public DataSourceSupplier(Supplier<List<T>> queryHandler) {
        if (queryHandler == null)
            throw new NullPointerException("数据提供者参数不合法");
        this.queryHandler = queryHandler;
    }

    /**
     * 查询数据
     *
     * @return 数据集合
     */
    public List<T> supplier() {
        //无参查询
        if (queryHandler != null) {
            final List<T> ts = queryHandler.get();
            return CollUtil.isEmpty(ts) ? Collections.emptyList() : ts;
        }
        //有参分页查询
        if (!hasData)
            return Collections.emptyList();
        final List<T> rs = queryParamHandler.apply(lastCursor, size);
        if (CollUtil.isEmpty(rs)) {
            hasData = false;
            return Collections.emptyList();
        }
        //尾游标设置
        final T t = rs.get(rs.size() - 1);
        lastCursor = String.valueOf(cursor.apply(t));
        return rs;
    }

}
