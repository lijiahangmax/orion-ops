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
package cn.orionsec.ops.entity.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统配置响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/18 22:53
 */
@Data
@ApiModel(value = "系统配置响应")
@SuppressWarnings("ALL")
public class SystemOptionVO {

    /**
     * @see cn.orionsec.ops.constant.system.SystemEnvAttr#ENABLE_AUTO_CLEAN_FILE
     */
    @ApiModelProperty(value = "是否启用自动清理")
    private Boolean autoCleanFile;

    /**
     * @see cn.orionsec.ops.constant.system.SystemEnvAttr#FILE_CLEAN_THRESHOLD
     */
    @ApiModelProperty(value = "文件清理阈值")
    private Integer fileCleanThreshold;

    /**
     * @see cn.orionsec.ops.constant.system.SystemEnvAttr#ALLOW_MULTIPLE_LOGIN
     */
    @ApiModelProperty(value = "允许多端登录")
    private Boolean allowMultipleLogin;

    /**
     * @see cn.orionsec.ops.constant.system.SystemEnvAttr#LOGIN_FAILURE_LOCK
     */
    @ApiModelProperty(value = "是否启用登录失败锁定")
    private Boolean loginFailureLock;

    /**
     * @see cn.orionsec.ops.constant.system.SystemEnvAttr#LOGIN_IP_BIND
     */
    @ApiModelProperty(value = "是否启用登录IP绑定")
    private Boolean loginIpBind;

    /**
     * @see cn.orionsec.ops.constant.system.SystemEnvAttr#LOGIN_TOKEN_AUTO_RENEW
     */
    @ApiModelProperty(value = "是否启用登录IP绑定")
    private Boolean loginTokenAutoRenew;

    /**
     * @see cn.orionsec.ops.constant.system.SystemEnvAttr#LOGIN_TOKEN_EXPIRE
     */
    @ApiModelProperty(value = "登录凭证有效期")
    private Integer loginTokenExpire;

    /**
     * @see cn.orionsec.ops.constant.system.SystemEnvAttr#LOGIN_FAILURE_LOCK_THRESHOLD
     */
    @ApiModelProperty(value = "登录失败锁定阈值")
    private Integer loginFailureLockThreshold;

    /**
     * @see cn.orionsec.ops.constant.system.SystemEnvAttr#LOGIN_TOKEN_AUTO_RENEW_THRESHOLD
     */
    @ApiModelProperty(value = "登录自动续签阈值")
    private Integer loginTokenAutoRenewThreshold;

    /**
     * @see cn.orionsec.ops.constant.system.SystemEnvAttr#RESUME_ENABLE_SCHEDULER_TASK
     */
    @ApiModelProperty(value = "自动恢复启用的调度任务")
    private Boolean resumeEnableSchedulerTask;

    /**
     * @see cn.orionsec.ops.constant.system.SystemEnvAttr#TERMINAL_ACTIVE_PUSH_HEARTBEAT
     */
    @ApiModelProperty(value = "终端后台主动推送心跳")
    private Boolean terminalActivePushHeartbeat;

    /**
     * @see cn.orionsec.ops.constant.system.SystemEnvAttr#SFTP_UPLOAD_THRESHOLD
     */
    @ApiModelProperty(value = "SFTP 上传文件最大阈值 (MB)")
    private Integer sftpUploadThreshold;

    /**
     * @see cn.orionsec.ops.constant.system.SystemEnvAttr#STATISTICS_CACHE_EXPIRE
     */
    @ApiModelProperty(value = "统计缓存有效时间 (分)")
    private Integer statisticsCacheExpire;

}
