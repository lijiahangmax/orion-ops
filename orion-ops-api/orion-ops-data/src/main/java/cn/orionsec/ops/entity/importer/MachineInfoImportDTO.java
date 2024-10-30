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
package cn.orionsec.ops.entity.importer;

import cn.orionsec.ops.constant.machine.MachineAuthType;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.vo.data.DataImportCheckRowVO;
import cn.orionsec.ops.utils.ValueMix;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.convert.TypeStore;
import com.orion.office.excel.annotation.ImportField;
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
     * @see MachineAuthType
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

    @ApiModelProperty(value = "密钥名称")
    @ImportField(index = 8)
    private String keyName;

    @ApiModelProperty(value = "描述")
    @ImportField(index = 9)
    private String description;

    @ApiModelProperty(value = "密码密文解密后的明文", hidden = true)
    private String decryptPassword;

    @ApiModelProperty(value = "密钥id", hidden = true)
    private Long keyId;

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
            d.setKeyId(p.keyId);
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
