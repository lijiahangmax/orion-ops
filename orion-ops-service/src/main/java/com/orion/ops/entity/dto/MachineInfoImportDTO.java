package com.orion.ops.entity.dto;

import com.orion.office.excel.annotation.ImportField;
import com.orion.ops.consts.machine.MachineAuthType;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.vo.DataImportCheckRowVO;
import com.orion.ops.utils.ValueMix;
import com.orion.utils.Strings;
import com.orion.utils.convert.TypeStore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 机器导入数据
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 14:01
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MachineInfoImportDTO extends BaseDataImportDTO {

    /**
     * 机器名称
     */
    @ImportField(index = 0)
    private String name;

    /**
     * 机器标签
     */
    @ImportField(index = 1)
    private String tag;

    /**
     * 机器主机
     */
    @ImportField(index = 2)
    private String host;

    /**
     * ssh 端口
     */
    @ImportField(index = 3)
    private Integer port;

    /**
     * 用户名
     */
    @ImportField(index = 4)
    private String username;

    /**
     * 密码 (密文)
     */
    @ImportField(index = 5)
    private String encryptPassword;

    /**
     * 导入密码 (明文)
     */
    @ImportField(index = 6)
    private String importPassword;

    /**
     * 描述
     */
    @ImportField(index = 7)
    private String description;

    /**
     * 密码密文解密后的明文
     */
    private String decryptPassword;

    static {
        TypeStore.STORE.register(MachineInfoImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.getTag());
            vo.setIllegalMessage(p.getIllegalMessage());
            return vo;
        });
        TypeStore.STORE.register(MachineInfoImportDTO.class, MachineInfoDO.class, p -> {
            MachineInfoDO d = new MachineInfoDO();
            d.setMachineName(p.getName());
            d.setMachineTag(p.getTag());
            d.setMachineHost(p.getHost());
            d.setSshPort(p.getPort());
            d.setDescription(p.getDescription());
            d.setUsername(p.getUsername());
            if (!Strings.isBlank(p.getDecryptPassword())) {
                d.setPassword(ValueMix.encrypt(p.getDecryptPassword()));
            }
            if (!Strings.isBlank(p.getImportPassword())) {
                d.setPassword(ValueMix.encrypt(p.getImportPassword()));
            }
            if (d.getPassword() == null) {
                d.setAuthType(MachineAuthType.KEY.getType());
            } else {
                d.setAuthType(MachineAuthType.PASSWORD.getType());
            }
            return d;
        });
    }

}
