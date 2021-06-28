package com.orion.ops.consts.sftp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * sftp通知类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/27 0:57
 */
@Getter
@AllArgsConstructor
public enum SftpNotifyType {

    /**
     * 添加任务
     */
    ADD(10),

    /**
     * 进度 以及速率
     */
    PROGRESS(20),

    /**
     * 修改状态
     */
    CHANGE_STATUS(30),

    ;

    Integer type;

}
