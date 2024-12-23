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
package cn.orionsec.ops.constant.webhook;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * webhook 类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/23 17:39
 */
@Getter
@AllArgsConstructor
public enum WebhookType {

    /**
     * 钉钉机器人
     */
    DING_ROBOT(10, "钉钉机器人"),

    ;

    private final Integer type;

    private final String label;

    public static WebhookType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (WebhookType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static WebhookType of(String label) {
        if (label == null) {
            return null;
        }
        for (WebhookType value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        return null;
    }

}
