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
package cn.orionsec.ops.entity.request.webhook;

import cn.orionsec.kit.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * webhook 配置请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/23 18:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "webhook 配置请求")
@SuppressWarnings("ALL")
public class WebhookConfigRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "url")
    private String url;

    /**
     * @see cn.orionsec.ops.constant.webhook.WebhookType
     */
    @ApiModelProperty(value = "类型 10: 钉钉机器人")
    private Integer type;

    @ApiModelProperty(value = "配置项 json")
    private String config;

}