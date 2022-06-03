package com.orion.ops.entity.dto.importer;

import com.orion.office.excel.annotation.ImportField;
import com.orion.ops.consts.machine.MachineAuthType;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.vo.DataImportCheckRowVO;
import com.orion.ops.utils.ValueMix;
import com.orion.utils.Strings;
import com.orion.utils.convert.TypeStore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

/**
 * 机器信息导入
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
     * 唯一标识
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
     * 认证方式
     */
    @ImportField(index = 4)
    private String authType;

    /**
     * 用户名
     */
    @ImportField(index = 5)
    private String username;

    /**
     * 密码 (密文)
     */
    @ImportField(index = 6)
    private String encryptPassword;

    /**
     * 导入密码 (明文)
     */
    @ImportField(index = 7)
    private String importPassword;

    /**
     * 描述
     */
    @ImportField(index = 8)
    private String description;

    /**
     * 密码密文解密后的明文
     */
    private String decryptPassword;

    static {
        TypeStore.STORE.register(MachineInfoImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.tag);
            vo.setIllegalMessage(p.getIllegalMessage());
            vo.setId(p.getId());
            return vo;
        });
        TypeStore.STORE.register(MachineInfoImportDTO.class, MachineInfoDO.class, p -> {
            MachineInfoDO d = new MachineInfoDO();
            d.setId(p.getId());
            d.setMachineName(p.name);
            d.setMachineTag(p.tag);
            d.setMachineHost(p.host);
            d.setSshPort(p.port);
            Optional.ofNullable(p.authType)
                    .map(MachineAuthType::of)
                    .map(MachineAuthType::getType)
                    .ifPresent(d::setAuthType);
            d.setUsername(p.username);
            if (MachineAuthType.PASSWORD.getType().equals(d.getAuthType())) {
                if (!Strings.isBlank(p.decryptPassword)) {
                    d.setPassword(ValueMix.encrypt(p.decryptPassword));
                }
                if (!Strings.isBlank(p.importPassword)) {
                    d.setPassword(ValueMix.encrypt(p.importPassword));
                }
            }
            d.setDescription(p.description);
            return d;
        });
    }

}
