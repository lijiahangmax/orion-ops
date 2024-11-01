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
package cn.orionsec.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 应用发布响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/20 9:43
 */
@Data
@ApiModel(value = "应用发布响应")
@SuppressWarnings("ALL")
public class ApplicationReleaseListVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "发布标题")
    private String title;

    @ApiModelProperty(value = "发布描述")
    private String description;

    @ApiModelProperty(value = "构建id")
    private Long buildId;

    @ApiModelProperty(value = "构建序列")
    private Integer buildSeq;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "应用唯一标识")
    private String appTag;

    /**
     * @see cn.orionsec.ops.constant.app.ReleaseType
     */
    @ApiModelProperty(value = "发布类型 10正常发布 20回滚发布")
    private Integer type;

    /**
     * @see cn.orionsec.ops.constant.app.ReleaseStatus
     */
    @ApiModelProperty(value = "发布状态 10待审核 20审核驳回 30待发布 35待调度 40发布中 50发布完成 60发布停止 70发布失败")
    private Integer status;

    /**
     * @see cn.orionsec.ops.constant.common.SerialType
     */
    @ApiModelProperty(value = "发布序列 10串行 20并行")
    private Integer serializer;

    /**
     * @see cn.orionsec.ops.constant.common.ExceptionHandlerType
     */
    @ApiModelProperty(value = "异常处理 10跳过所有 20跳过错误")
    private Integer exceptionHandler;

    /**
     * @see cn.orionsec.ops.constant.app.TimedType
     */
    @ApiModelProperty(value = "是否是定时发布 10普通发布 20定时发布")
    private Integer timedRelease;

    @ApiModelProperty(value = "定时发布时间")
    private Date timedReleaseTime;

    @ApiModelProperty(value = "创建人名称")
    private String createUserName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "审核人名称")
    private String auditUserName;

    @ApiModelProperty(value = "审核备注")
    private String auditReason;

    @ApiModelProperty(value = "审核时间")
    private Date auditTime;

    @ApiModelProperty(value = "发布人")
    private String releaseUserName;

    @ApiModelProperty(value = "发布时间")
    private Date releaseTime;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "发布机器")
    private List<ApplicationReleaseMachineVO> machines;

}
