/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.service.api;

import cn.orionsec.ops.entity.request.data.DataClearRequest;

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

    /**
     * 清理 机器报警历史
     *
     * @param request request
     * @return 删除数量
     */
    Integer clearMachineAlarmHistory(DataClearRequest request);

}
