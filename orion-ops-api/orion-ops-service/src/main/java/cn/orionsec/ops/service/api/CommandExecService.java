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

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.ops.entity.domain.CommandExecDO;
import cn.orionsec.ops.entity.request.exec.CommandExecRequest;
import cn.orionsec.ops.entity.vo.exec.CommandExecStatusVO;
import cn.orionsec.ops.entity.vo.exec.CommandExecVO;
import cn.orionsec.ops.entity.vo.exec.CommandTaskSubmitVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 命令执行接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 17:54
 */
public interface CommandExecService {

    /**
     * 提交命令
     *
     * @param request request
     * @return tasks
     */
    List<CommandTaskSubmitVO> batchSubmitTask(CommandExecRequest request);

    /**
     * 命令执行列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<CommandExecVO> execList(@RequestBody CommandExecRequest request);

    /**
     * 命令执行详情
     *
     * @param id id
     * @return detail
     */
    CommandExecVO execDetail(Long id);

    /**
     * 写入命令
     *
     * @param id      id
     * @param command command
     */
    void writeCommand(Long id, String command);

    /**
     * 终止任务
     *
     * @param id id
     */
    void terminateExec(Long id);

    /**
     * 删除任务
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteTask(List<Long> idList);

    /**
     * 查询状态
     *
     * @param execIdList execIdList
     * @return status
     */
    List<CommandExecStatusVO> getExecStatusList(List<Long> execIdList);

    /**
     * 通过id查询 execDO
     *
     * @param id id
     * @return rows
     */
    CommandExecDO selectById(Long id);

    /**
     * 获取 exec command 日志
     *
     * @param id id
     * @return logPath
     */
    String getExecLogFilePath(Long id);

}
