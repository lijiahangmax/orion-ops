package com.orion.ops.consts.command;

import com.orion.ops.entity.dto.importer.CommandTemplateImportDTO;
import com.orion.ops.utils.Valid;
import com.orion.utils.Strings;

/**
 * 命令模板字段常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/1 10:42
 */
public class CommandTemplateFieldConst {

    private CommandTemplateFieldConst() {
    }

    public static final int NAME_MAX_LEN = 32;

    public static final int TEMPLATE_MAX_LEN = 2048;

    public static final int DESCRIPTION_MAX_LEN = 64;

    public static final String NAME_EMPTY_MESSAGE = "模板名称不能为空";

    public static final String NAME_LEN_MESSAGE = "模板名称长度不能大于 " + NAME_MAX_LEN + "位";

    public static final String TEMPLATE_EMPTY_MESSAGE = "模板命令不能为空";

    public static final String TEMPLATE_LEN_MESSAGE = "模板命令长度不能大于 " + TEMPLATE_MAX_LEN + "位";

    public static final String DESCRIPTION_LEN_MESSAGE = "描述长度不能大于 " + DESCRIPTION_MAX_LEN + "位";

    /**
     * 验证数据合法性
     *
     * @param o o
     */
    public static void validData(Object o) {
        if (o instanceof CommandTemplateImportDTO) {
            validImport((CommandTemplateImportDTO) o);
        }
    }

    /**
     * 验证导入数据
     *
     * @param row row
     */
    public static void validImport(CommandTemplateImportDTO row) {
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
