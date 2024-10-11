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
package com.orion.ops.handler.importer.validator;

import com.orion.ops.constant.webhook.WebhookType;
import com.orion.ops.entity.importer.WebhookImportDTO;
import com.orion.ops.utils.Valid;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * webhook 表数据验证器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/13 16:09
 */
public class WebhookValidator implements DataValidator {

    private WebhookValidator() {
    }

    public static final WebhookValidator INSTANCE = new WebhookValidator();

    public static final int NAME_MAX_LEN = 64;

    public static final int URL_MAX_LEN = 2048;

    public static final String NAME_EMPTY_MESSAGE = "名称不能为空";

    public static final String NAME_LEN_MESSAGE = "名称长度不能大于 " + NAME_MAX_LEN + "位";

    public static final String TYPE_EMPTY_MESSAGE = "类型不能为空";

    public static final String TYPE_MESSAGE = "类型只能为 " + Arrays.stream(WebhookType.values()).map(WebhookType::getLabel).collect(Collectors.toList());

    public static final String URL_EMPTY_MESSAGE = "url不能为空";

    public static final String URL_LEN_MESSAGE = "url长度不能大于 " + URL_MAX_LEN + "位";

    @Override
    public void validData(Object o) {
        if (o instanceof WebhookImportDTO) {
            validImport((WebhookImportDTO) o);
        }
    }

    /**
     * 验证导入数据
     *
     * @param row row
     */
    private void validImport(WebhookImportDTO row) {
        String name = row.getName();
        String type = row.getType();
        String url = row.getUrl();
        Valid.notBlank(name, NAME_EMPTY_MESSAGE);
        Valid.validLengthLte(name, NAME_MAX_LEN, NAME_LEN_MESSAGE);
        Valid.notBlank(type, TYPE_EMPTY_MESSAGE);
        Valid.notNull(WebhookType.of(type), TYPE_MESSAGE);
        Valid.notBlank(url, URL_EMPTY_MESSAGE);
        Valid.validLengthLte(url, URL_MAX_LEN, URL_LEN_MESSAGE);
    }

}
