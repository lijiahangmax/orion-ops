package com.orion.ops.entity.request.sftp;

import lombok.Data;

import java.util.List;

/**
 * 数据清理请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/24 16:36
 */
@Data
public class DataClearRequest {

    /**
     * 保留天数
     */
    private Integer reserveDay;

    /**
     * 保留条数
     */
    private Integer reserveTotal;

    /**
     * 清理区间
     *
     * @see com.orion.ops.consts.clear.DataClearRange
     */
    private Integer range;

    /**
     * 清理的引用id
     */
    private List<Long> relIdList;

    /**
     * 引用id
     */
    private Long relId;

    /**
     * profile id
     */
    private Long profileId;

    /**
     * 只清理我创建的
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    private Integer iCreated;

    /**
     * 只清理我审核的
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    private Integer iAudited;

    /**
     * 只清理我执行的
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    private Integer iExecute;

    /**
     * 只清理未读的
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    private Integer onlyRead;

}
