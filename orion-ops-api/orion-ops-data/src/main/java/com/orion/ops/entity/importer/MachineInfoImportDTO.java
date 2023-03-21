package com.orion.ops.entity.importer;

import com.orion.lang.utils.Strings;
import com.orion.lang.utils.convert.TypeStore;
import com.orion.office.excel.annotation.ImportField;
import com.orion.ops.constant.machine.MachineAuthType;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.vo.data.DataImportCheckRowVO;
import com.orion.ops.utils.ValueMix;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "机器信息导入")
public class MachineInfoImportDTO extends BaseDataImportDTO {

    @ApiModelProperty(value = "机器名称")
    @ImportField(index = 0)
    private String name;

    @ApiModelProperty(value = "唯一标识")
    @ImportField(index = 1)
    private String tag;

    @ApiModelProperty(value = "机器主机")
    @ImportField(index = 2)
    private String host;

    @ApiModelProperty(value = "ssh 端口")
    @ImportField(index = 3)
    private Integer port;

    /**
     * @see com.orion.ops.constant.machine.MachineAuthType
     */
    @ApiModelProperty(value = "认证方式")
    @ImportField(index = 4)
    private String authType;

    @ApiModelProperty(value = "用户名")
    @ImportField(index = 5)
    private String username;

    @ApiModelProperty(value = "密码 (密文)")
    @ImportField(index = 6)
    private String encryptPassword;

    @ApiModelProperty(value = "导入密码 (明文)")
    @ImportField(index = 7)
    private String importPassword;

    @ApiModelProperty(value = "秘钥名称")
    @ImportField(index = 8)
    private String usingKeyName;

    @ApiModelProperty(value = "描述")
    @ImportField(index = 9)
    private String description;

    @ApiModelProperty(value = "密码密文解密后的明文", hidden = true)
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
