/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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
package com.orion.ops.handler.app.action;

import com.orion.lang.utils.collect.Maps;
import com.orion.net.remote.channel.SessionStore;
import com.orion.ops.entity.domain.ApplicationActionLogDO;
import lombok.Data;

import java.io.OutputStream;
import java.util.Map;

/**
 * 发布操作参数
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/11 16:05
 */
@Data
public class MachineActionStore {

    /**
     * 引用id
     */
    private Long relId;

    /**
     * action
     */
    private Map<Long, ApplicationActionLogDO> actions;

    /**
     * 日志输出流
     */
    private OutputStream superLogStream;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器用户
     */
    private String machineUsername;

    /**
     * 机器主机
     */
    private String machineHost;

    /**
     * 机器会话
     */
    private SessionStore sessionStore;

    /**
     * 版本id
     *
     * @see CheckoutActionHandler
     */
    private Long repoId;

    /**
     * 分支
     *
     * @see CheckoutActionHandler
     */
    private String branchName;

    /**
     * 提交版本
     *
     * @see CheckoutActionHandler
     */
    private String commitId;

    /**
     * 仓库 clone 路径
     *
     * @see CheckoutActionHandler
     */
    private String repoClonePath;

    /**
     * 构建产物文件
     *
     * @see SftpTransferActionHandler
     * @see ScpTransferActionHandler
     */
    private String bundlePath;

    /**
     * 产物传输路径
     *
     * @see SftpTransferActionHandler
     * @see ScpTransferActionHandler
     */
    private String transferPath;

    /**
     * 产物传输方式
     *
     * @see SftpTransferActionHandler
     * @see ScpTransferActionHandler
     * @see com.orion.ops.constant.app.TransferMode
     */
    private String transferMode;

    public MachineActionStore() {
        this.actions = Maps.newLinkedMap();
    }

}
