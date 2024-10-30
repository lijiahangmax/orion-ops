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
package cn.orionsec.ops.entity.vo.history;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 历史值快照响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 19:03
 */
@Data
@ApiModel(value = "历史值快照响应")
@SuppressWarnings("ALL")
public class HistoryValueVO {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * @see cn.orionsec.ops.constant.history.HistoryOperator
     */
    @ApiModelProperty(value = "操作类型 1新增 2修改 3删除")
    private Integer type;

    @ApiModelProperty(value = "原始值")
    private String beforeValue;

    @ApiModelProperty(value = "新值")
    private String afterValue;

    @ApiModelProperty(value = "修改人id")
    private Long updateUserId;

    @ApiModelProperty(value = "修改人用户名")
    private String updateUserName;

    @ApiModelProperty(value = "修改时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private String createTimeAgo;

}
