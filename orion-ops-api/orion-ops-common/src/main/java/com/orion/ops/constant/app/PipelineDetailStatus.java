package com.orion.ops.constant.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流水香详情状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/8 9:52
 */
@AllArgsConstructor
@Getter
public enum PipelineDetailStatus {

    /**
     * 未开始
     */
    WAIT(10),

    /**
     * 进行中
     */
    RUNNABLE(20),

    /**
     * 已完成
     */
    FINISH(30),

    /**
     * 执行失败
     */
    FAILURE(40),

    /**
     * 已跳过
     */
    SKIPPED(50),

    /**
     * 已终止
     */
    TERMINATED(60),

    ;

    private final Integer status;

    public static PipelineDetailStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (PipelineDetailStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
