package com.orion.ops.handler.alarm.push;

/**
 * 报警推送器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/29 19:00
 */
public interface IAlarmPusher {

    /**
     * 执行推送
     */
    void push();
}
