package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 数据导出请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 16:10
 */
@Data
public class DataExportRequest {

    /**
     * 是否导出密码
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    private Integer exportPassword;

    /**
     * 保护密码
     */
    private String protectPassword;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 分类
     */
    private Integer classify;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 只看自己
     *
     * @see com.orion.ops.consts.Const#ENABLE
     */
    private Integer onlyMyself;

}
