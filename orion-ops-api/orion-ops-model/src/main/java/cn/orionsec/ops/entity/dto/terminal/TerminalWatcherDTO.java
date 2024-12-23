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
package cn.orionsec.ops.entity.dto.terminal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 终端监视信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/29 10:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "终端监视信息")
@SuppressWarnings("ALL")
public class TerminalWatcherDTO {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "会话token")
    private String token;

    /**
     * @see cn.orionsec.ops.constant.Const#ENABLE
     */
    @ApiModelProperty(value = "是否只读")
    private Integer readonly;

}
