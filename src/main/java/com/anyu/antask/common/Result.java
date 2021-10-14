package com.anyu.antask.common;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anyu
 * @version 1.0.0
 * @since 2021/8/22
 */
@Getter
@ToString
public class Result {
    private int sTotal;
    private int fTotal;
    private int cTotal;
    private List<String> eMessages;

    /**
     * 添加异常信息
     *
     * @param exceptionMsg 异常信息
     */
    public void addExceptionMsg(String exceptionMsg) {
        if (exceptionMsg == null)
            return;
        if (eMessages == null)
            eMessages = new ArrayList<>();
        eMessages.add(exceptionMsg);
    }

    /**
     * 添加生产资源数
     *
     * @param num 生产资源数
     */
    public void addSTotal(int num) {
        if (num < 1)
            return;
        this.sTotal += num;
    }

    /**
     * 添加过滤资源数
     *
     * @param num 滤资源数
     */
    public void addFTotal(int num) {
        if (num < 1)
            return;
        this.fTotal += num;
    }

    /**
     * 添加消费资源数
     *
     * @param num 消费资源数
     */
    public void addCTotal(int num) {
        if (num < 1)
            return;
        this.cTotal += num;
    }
}
