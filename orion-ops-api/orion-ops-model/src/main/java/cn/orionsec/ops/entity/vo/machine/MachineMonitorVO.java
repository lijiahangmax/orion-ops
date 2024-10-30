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
package cn.orionsec.ops.entity.vo.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机器代理响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 22:01
 */
@Data
@ApiModel(value = "机器代理响应")
@SuppressWarnings("ALL")
public class MachineMonitorVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器主机")
    private String machineHost;

    /**
     * @see cn.orionsec.ops.constant.monitor.MonitorStatus
     */
    @ApiModelProperty(value = "监控状态 1未安装 2安装中 3未运行 4运行中")
    private Integer status;

    @ApiModelProperty("机器监控 url")
    private String url;

    @ApiModelProperty("请求 accessToken")
    private String accessToken;

    @ApiModelProperty("当前插件版本")
    private String currentVersion;

    @ApiModelProperty("最新插件版本")
    private String latestVersion;

}
