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
package cn.orionsec.ops.entity.request.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 重置密码请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/21 23:13
 */
@Data
@ApiModel(value = "重置密码请求")
public class UserResetRequest {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "原密码")
    private String beforePassword;

    @ApiModelProperty(value = "密码")
    private String password;

}
