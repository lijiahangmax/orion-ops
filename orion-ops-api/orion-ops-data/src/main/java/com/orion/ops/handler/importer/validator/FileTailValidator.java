package com.orion.ops.handler.importer.validator;

import com.orion.lang.utils.Charsets;
import com.orion.ops.entity.importer.MachineTailFileImportDTO;
import com.orion.ops.utils.Valid;

/**
 * 文件tail 数据验证器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/31 16:39
 */
public class FileTailValidator implements DataValidator {

    private FileTailValidator() {
    }

    public static final FileTailValidator INSTANCE = new FileTailValidator();

    public static final int NAME_MAX_LEN = 64;

    public static final int PATH_MAX_LEN = 1024;

    public static final int COMMAND_MAX_LEN = 1024;

    public static final int CHARSET_MAX_LEN = 16;

    public static final int OFFSET_MIN_RANGE = 0;

    public static final int OFFSET_MAX_RANGE = 10000;

    public static final int DESCRIPTION_MAX_LEN = 64;

    public static final String MACHINE_TAG_EMPTY_MESSAGE = "机器标识不能为空";

    public static final String NAME_EMPTY_MESSAGE = "文件别名不能为空";

    public static final String NAME_LEN_MESSAGE = "文件别名长度不能大于 " + NAME_MAX_LEN + "位";

    public static final String PATH_EMPTY_MESSAGE = "文件路径不能为空";

    public static final String PATH_LEN_MESSAGE = "文件路径长度不能大于 " + PATH_MAX_LEN + "位";

    public static final String COMMAND_EMPTY_MESSAGE = "执行命令不能为空";

    public static final String COMMAND_LEN_MESSAGE = "执行命令长度不能大于 " + COMMAND_MAX_LEN + "位";

    public static final String CHARSET_EMPTY_MESSAGE = "文件编码不能为空";

    public static final String CHARSET_UNSUPPORTED = "文件编码不合法";

    public static final String CHARSET_LEN_MESSAGE = "文件编码不能大于 " + CHARSET_MAX_LEN + "位";

    public static final String OFFSET_EMPTY_MESSAGE = "尾部偏移行不能为空";

    public static final String OFFSET_LEN_MESSAGE = "尾部偏移行必须在" + OFFSET_MIN_RANGE + "~" + OFFSET_MAX_RANGE + "之间";

    public static final String DESCRIPTION_LEN_MESSAGE = "描述长度不能大于 " + DESCRIPTION_MAX_LEN + "位";

    @Override
    public void validData(Object o) {
        if (o instanceof MachineTailFileImportDTO) {
            validImport((MachineTailFileImportDTO) o);
        }
    }

    /**
     * 验证导入数据
     *
     * @param row row
     */
    private void validImport(MachineTailFileImportDTO row) {
        String machineTag = row.getMachineTag();
        String name = row.getName();
        String path = row.getPath();
        String charset = row.getCharset();
        Integer offset = row.getOffset();
        String command = row.getCommand();
        Valid.notBlank(machineTag, MACHINE_TAG_EMPTY_MESSAGE);
        Valid.notBlank(name, NAME_EMPTY_MESSAGE);
        Valid.validLengthLte(name, NAME_MAX_LEN, NAME_LEN_MESSAGE);
        Valid.notBlank(path, PATH_EMPTY_MESSAGE);
        Valid.validLengthLte(path, PATH_MAX_LEN, PATH_LEN_MESSAGE);
        Valid.notBlank(charset, CHARSET_EMPTY_MESSAGE);
        Valid.validLengthLte(charset, CHARSET_MAX_LEN, CHARSET_LEN_MESSAGE);
        Valid.isTrue(Charsets.isSupported(charset), CHARSET_UNSUPPORTED);
        Valid.notNull(offset, OFFSET_EMPTY_MESSAGE);
        Valid.inRange(offset, OFFSET_MIN_RANGE, OFFSET_MAX_RANGE, OFFSET_LEN_MESSAGE);
        Valid.notBlank(command, COMMAND_EMPTY_MESSAGE);
        Valid.validLengthLte(command, COMMAND_MAX_LEN, COMMAND_LEN_MESSAGE);
    }
}
