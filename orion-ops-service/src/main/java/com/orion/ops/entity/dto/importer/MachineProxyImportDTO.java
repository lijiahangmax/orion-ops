package com.orion.ops.entity.dto.importer;

import com.orion.office.excel.annotation.ImportField;
import com.orion.ops.consts.machine.ProxyType;
import com.orion.ops.entity.domain.MachineProxyDO;
import com.orion.ops.entity.vo.DataImportCheckRowVO;
import com.orion.ops.utils.ValueMix;
import com.orion.utils.Strings;
import com.orion.utils.convert.TypeStore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

/**
 * 机器代理导入
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/31 9:54
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MachineProxyImportDTO extends BaseDataImportDTO {

    /**
     * 代理主机
     */
    @ImportField(index = 0)
    private String host;

    /**
     * 代理端口
     */
    @ImportField(index = 1)
    private Integer port;

    /**
     * 代理类型
     */
    @ImportField(index = 2)
    private String proxyType;

    /**
     * 用户名
     */
    @ImportField(index = 3)
    private String username;

    /**
     * 导出密码 (密文)
     */
    @ImportField(index = 4)
    private String encryptPassword;

    /**
     * 导入密码 (明文)
     */
    @ImportField(index = 5)
    private String importPassword;

    /**
     * 描述
     */
    @ImportField(index = 6)
    private String description;

    /**
     * 密码密文解密后的明文
     */
    private String decryptPassword;

    static {
        TypeStore.STORE.register(MachineProxyImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.host);
            vo.setIllegalMessage(p.getIllegalMessage());
            return vo;
        });
        TypeStore.STORE.register(MachineProxyImportDTO.class, MachineProxyDO.class, p -> {
            MachineProxyDO d = new MachineProxyDO();
            d.setProxyHost(p.host);
            d.setProxyPort(p.port);
            d.setProxyUsername(p.username);
            if (!Strings.isBlank(p.decryptPassword)) {
                d.setProxyPassword(ValueMix.encrypt(p.decryptPassword));
            }
            if (!Strings.isBlank(p.importPassword)) {
                d.setProxyPassword(ValueMix.encrypt(p.importPassword));
            }
            Optional.ofNullable(p.proxyType)
                    .map(ProxyType::of)
                    .map(ProxyType::getType)
                    .ifPresent(d::setProxyType);
            d.setDescription(p.description);
            return d;
        });
    }

}
