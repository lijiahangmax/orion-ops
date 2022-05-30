package com.orion.ops.consts.machine;

import com.orion.ops.entity.dto.importer.MachineInfoImportDTO;
import com.orion.ops.utils.Valid;
import com.orion.ops.utils.ValueMix;
import com.orion.utils.Strings;

/**
 * 机器表字段常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/27 13:49
 */
public class MachineFieldConst {

    private MachineFieldConst() {
    }

    public static final int NAME_MAX_LEN = 32;

    public static final int TAG_MAX_LEN = 32;

    public static final int HOST_MAX_LEN = 128;

    public static final int USER_NAME_MAX_LEN = 32;

    public static final int PASSWORD_MAX_LEN = 128;

    public static final int PORT_MAX_RANGE = 2;

    public static final int PORT_MIN_RANGE = 65534;

    public static final int DESCRIPTION_MAX_LEN = 64;

    public static final String NAME_EMPTY_MESSAGE = "名称不能为空";

    public static final String NAME_LEN_MESSAGE = "名称长度不能大于 " + NAME_MAX_LEN + "位";

    public static final String TAG_EMPTY_MESSAGE = "唯一标识不能为空";

    public static final String TAG_LEN_MESSAGE = "唯一标识长度不能大于 " + TAG_MAX_LEN + "位";

    public static final String HOST_EMPTY_MESSAGE = "主机不能为空";

    public static final String HOST_LEN_MESSAGE = "主机长度不能大于 " + HOST_MAX_LEN + "位";

    public static final String AUTH_TYPE_EMPTY_MESSAGE = "认证方式不能为空";

    public static final String AUTH_TYPE_MESSAGE = "认证方式只能为 " + MachineAuthType.PASSWORD.getLabel() + "和" + MachineAuthType.KEY.getLabel();

    public static final String USER_NAME_EMPTY_MESSAGE = "用户名不能为空";

    public static final String USER_NAME_LEN_MESSAGE = "用户名长度不能大于 " + USER_NAME_MAX_LEN + "位";

    public static final String PASSWORD_DECRYPT_MESSAGE = "密码密文解密失败";

    public static final String PASSWORD_LEN_MESSAGE = "密码明文长度不能大于 " + PASSWORD_MAX_LEN + "位";

    public static final String PASSWORD_EMPTY_MESSAGE = "密码验证密码不能为空";

    public static final String PORT_EMPTY_MESSAGE = "端口不能为空";

    public static final String PORT_LEN_MESSAGE = "端口必须在" + PORT_MAX_RANGE + "~" + PORT_MIN_RANGE + "之间";

    public static final String DESCRIPTION_LEN_MESSAGE = "描述长度不能大于 " + DESCRIPTION_MAX_LEN + "位";

    /**
     * 验证数据合法性
     *
     * @param o o
     */
    public static void validData(Object o) {
        if (o instanceof MachineInfoImportDTO) {
            validImport((MachineInfoImportDTO) o);
        }
    }

    /**
     * 验证导入数据
     *
     * @param row row
     */
    public static void validImport(MachineInfoImportDTO row) {
        String name = row.getName();
        String tag = row.getTag();
        String host = row.getHost();
        Integer port = row.getPort();
        String username = row.getUsername();
        String authType = row.getAuthType();
        String encryptPassword = row.getEncryptPassword();
        String password = row.getImportPassword();
        String description = row.getDescription();
        Valid.notBlank(name, NAME_EMPTY_MESSAGE);
        Valid.validLengthLte(name, NAME_MAX_LEN, NAME_LEN_MESSAGE);
        Valid.notBlank(tag, TAG_EMPTY_MESSAGE);
        Valid.validLengthLte(tag, TAG_MAX_LEN, TAG_LEN_MESSAGE);
        Valid.notBlank(host, HOST_EMPTY_MESSAGE);
        Valid.validLengthLte(host, HOST_MAX_LEN, HOST_LEN_MESSAGE);
        Valid.notNull(port, PORT_EMPTY_MESSAGE);
        Valid.inRange(port, PORT_MAX_RANGE, PORT_MIN_RANGE, PORT_LEN_MESSAGE);
        Valid.notBlank(username, USER_NAME_EMPTY_MESSAGE);
        Valid.validLengthLte(username, USER_NAME_MAX_LEN, USER_NAME_LEN_MESSAGE);
        Valid.notBlank(authType, AUTH_TYPE_EMPTY_MESSAGE);
        MachineAuthType machineAuthType = Valid.notNull(MachineAuthType.of(authType), AUTH_TYPE_MESSAGE);
        if (!Strings.isBlank(encryptPassword)) {
            String decryptPassword = ValueMix.decrypt(encryptPassword);
            Valid.notNull(decryptPassword, PASSWORD_DECRYPT_MESSAGE);
            row.setDecryptPassword(decryptPassword);
        }
        if (!Strings.isBlank(password)) {
            Valid.validLengthLte(password, PASSWORD_MAX_LEN, PASSWORD_LEN_MESSAGE);
        }
        if (MachineAuthType.PASSWORD.equals(machineAuthType)) {
            Valid.isTrue(!Strings.isBlank(row.getImportPassword()) || !Strings.isBlank(row.getDecryptPassword()), PASSWORD_EMPTY_MESSAGE);
        }
        if (!Strings.isBlank(description)) {
            Valid.validLengthLte(description, DESCRIPTION_MAX_LEN, DESCRIPTION_LEN_MESSAGE);
        }
    }
}
