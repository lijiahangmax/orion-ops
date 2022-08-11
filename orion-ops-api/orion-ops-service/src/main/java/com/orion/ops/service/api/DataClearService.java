package com.orion.ops.service.api;

import com.orion.ops.entity.request.data.DataClearRequest;

/**
 * 数据清理服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/24 17:01
 */
public interface DataClearService {

    /**
     * 清理 批量执行数据
     *
     * @param request request
     * @return 删除数量
     */
    Integer clearBatchExec(DataClearRequest request);

    /**
     * 清理 终端日志
     *
     * @param request request
     * @return 删除数量
     */
    Integer clearTerminalLog(DataClearRequest request);

    /**
     * 清理 调度记录
     *
     * @param request request
     * @return 删除数量
     */
    Integer clearSchedulerRecord(DataClearRequest request);

    /**
     * 清理 应用构建
     *
     * @param request request
     * @return 删除数量
     */
    Integer clearAppBuild(DataClearRequest request);

    /**
     * 清理 应用发布
     *
     * @param request request
     * @return 删除数量
     */
    Integer clearAppRelease(DataClearRequest request);

    /**
     * 清理 应用流水线
     *
     * @param request request
     * @return 删除数量
     */
    Integer clearAppPipeline(DataClearRequest request);

    /**
     * 清理 操作日志
     *
     * @param request request
     * @return 删除数量
     */
    Integer clearEventLog(DataClearRequest request);

}
