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
package cn.orionsec.ops.entity.request.machine;

import cn.orionsec.kit.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 机器监控请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 18:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "机器监控请求")
@SuppressWarnings("ALL")
public class MachineMonitorRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    /**
     * @see cn.orionsec.ops.constant.monitor.MonitorStatus
     */
    @ApiModelProperty(value = "安装状态")
    private Integer status;

    @ApiModelProperty(value = "请求url")
    private String url;

    @ApiModelProperty(value = "accessToken")
    private String accessToken;

    @ApiModelProperty(value = "是否为升级")
    private Boolean upgrade;

}
