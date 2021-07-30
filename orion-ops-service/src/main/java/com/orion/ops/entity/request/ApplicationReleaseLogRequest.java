package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 上线单日志请求 (已完成的上线单获取最后几行)
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/29 18:46
 */
@Data
public class ApplicationReleaseLogRequest {

    /**
     * 上线单id
     */
    private Long releaseId;

    /**
     * 上线单机器id
     */
    private Long releaseMachineId;

}
