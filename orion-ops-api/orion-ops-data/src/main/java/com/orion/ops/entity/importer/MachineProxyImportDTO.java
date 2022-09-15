package com.orion.ops.entity.importer;

import com.orion.lang.utils.Strings;
import com.orion.lang.utils.convert.TypeStore;
import com.orion.office.excel.annotation.ImportField;
import com.orion.ops.constant.machine.ProxyType;
import com.orion.ops.entity.domain.MachineProxyDO;
import com.orion.ops.entity.vo.data.DataImportCheckRowVO;
import com.orion.ops.utils.ValueMix;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "机器代理导入")
public class MachineProxyImportDTO extends BaseDataImportDTO {

    @ApiModelProperty(value = "代理主机")
    @ImportField(index = 0)
    private String host;

    @ApiModelProperty(value = "代理端口")
    @ImportField(index = 1)
    private Integer port;

    /**
     * @see com.orion.ops.constant.machine.ProxyType
     */
    @ApiModelProperty(value = "代理类型")
    @ImportField(index = 2)
    private String proxyType;

    @ApiModelProperty(value = "用户名")
    @ImportField(index = 3)
    private String username;

    @ApiModelProperty(value = "导出密码 (密文)")
    @ImportField(index = 4)
    private String encryptPassword;

    @ApiModelProperty(value = "导入密码 (明文)")
    @ImportField(index = 5)
    private String importPassword;

    @ApiModelProperty(value = "描述")
    @ImportField(index = 6)
    private String description;

    @ApiModelProperty(value = "密码密文解密后的明文", hidden = true)
    private String decryptPassword;

    static {
        TypeStore.STORE.register(MachineProxyImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.host);
            vo.setIllegalMessage(p.getIllegalMessage());
            vo.setId(p.getId());
            return vo;
        });
        TypeStore.STORE.register(MachineProxyImportDTO.class, MachineProxyDO.class, p -> {
            MachineProxyDO d = new MachineProxyDO();
            d.setId(p.getId());
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
