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
package cn.orionsec.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用依赖机器表
 *
 * @author Jiahang Li
 * @since 2021-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "应用依赖机器表")
@TableName("application_machine")
public class ApplicationMachineDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "应用id")
    @TableField("app_id")
    private Long appId;

    @ApiModelProperty(value = "环境id")
    @TableField("profile_id")
    private Long profileId;

    @ApiModelProperty(value = "机器id")
    @TableField("machine_id")
    private Long machineId;

    @ApiModelProperty(value = "当前版本发布id")
    @TableField("release_id")
    private Long releaseId;

    @ApiModelProperty(value = "当前版本构建id")
    @TableField("build_id")
    private Long buildId;

    @ApiModelProperty(value = "当前版本构建序列")
    @TableField("build_seq")
    private Integer buildSeq;

    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
