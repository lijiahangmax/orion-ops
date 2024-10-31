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
package cn.orionsec.ops.handler.importer.validator;

import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.ops.entity.importer.CommandTemplateImportDTO;
import cn.orionsec.ops.utils.Valid;

/**
 * 命令模板 数据验证器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/1 10:42
 */
public class CommandTemplateValidator implements DataValidator {

    private CommandTemplateValidator() {
    }

    public static final CommandTemplateValidator INSTANCE = new CommandTemplateValidator();

    public static final int NAME_MAX_LEN = 32;

    public static final int TEMPLATE_MAX_LEN = 2048;

    public static final int DESCRIPTION_MAX_LEN = 64;

    public static final String NAME_EMPTY_MESSAGE = "模板名称不能为空";

    public static final String NAME_LEN_MESSAGE = "模板名称长度不能大于 " + NAME_MAX_LEN + "位";

    public static final String TEMPLATE_EMPTY_MESSAGE = "模板命令不能为空";

    public static final String TEMPLATE_LEN_MESSAGE = "模板命令长度不能大于 " + TEMPLATE_MAX_LEN + "位";

    public static final String DESCRIPTION_LEN_MESSAGE = "描述长度不能大于 " + DESCRIPTION_MAX_LEN + "位";

    @Override
    public void validData(Object o) {
        if (o instanceof CommandTemplateImportDTO) {
            validImport((CommandTemplateImportDTO) o);
        }
    }

    /**
     * 验证导入数据
     *
     * @param row row
     */
    private void validImport(CommandTemplateImportDTO row) {
        String name = row.getName();
        String template = row.getTemplate();
        String description = row.getDescription();
        Valid.notBlank(name, NAME_EMPTY_MESSAGE);
        Valid.validLengthLte(name, NAME_MAX_LEN, NAME_LEN_MESSAGE);
        Valid.notBlank(template, TEMPLATE_EMPTY_MESSAGE);
        Valid.validLengthLte(template, TEMPLATE_MAX_LEN, TEMPLATE_LEN_MESSAGE);
        if (!Strings.isBlank(description)) {
            Valid.validLengthLte(description, DESCRIPTION_MAX_LEN, DESCRIPTION_LEN_MESSAGE);
        }
    }
}
