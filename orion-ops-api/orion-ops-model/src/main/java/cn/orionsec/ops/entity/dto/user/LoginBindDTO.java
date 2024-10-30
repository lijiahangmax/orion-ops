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
package cn.orionsec.ops.entity.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 登录绑定信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/18 23:32
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "登录绑定信息")
public class LoginBindDTO implements Serializable {

    @ApiModelProperty(value = "登录时间戳")
    private Long timestamp;

    @ApiModelProperty(value = "登录 IP")
    private String loginIp;

}
