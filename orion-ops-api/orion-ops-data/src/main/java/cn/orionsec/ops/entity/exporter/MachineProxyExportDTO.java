/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
package cn.orionsec.ops.entity.exporter;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.office.excel.annotation.ExportField;
import cn.orionsec.kit.office.excel.annotation.ExportSheet;
import cn.orionsec.kit.office.excel.annotation.ExportTitle;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.machine.ProxyType;
import cn.orionsec.ops.entity.domain.MachineProxyDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Optional;

/**
 * 机器代理导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/30 10:18
 */
@Data
@ApiModel(value = "机器代理导出")
@ExportTitle(title = "机器代理导出")
@ExportSheet(name = "机器代理", height = 22, freezeHeader = true, filterHeader = true)
public class MachineProxyExportDTO {

    @ApiModelProperty(value = "代理主机")
    @ExportField(index = 0, header = "代理主机", width = 25)
    private String host;

    @ApiModelProperty(value = "代理端口")
    @ExportField(index = 1, header = "代理端口", width = 10)
    private Integer port;

    /**
     * @see ProxyType
     */
    @ApiModelProperty(value = "代理类型")
    @ExportField(index = 2, header = "代理类型", width = 13, selectOptions = {Const.PROTOCOL_HTTP, Const.SOCKS4, Const.SOCKS5})
    private String proxyType;

    @ApiModelProperty(value = "用户名")
    @ExportField(index = 3, header = "用户名", width = 25)
    private String username;

    @ApiModelProperty(value = "导出密码")
    @ExportField(index = 4, header = "导出密码", hidden = true, width = 16, wrapText = true)
    private String encryptPassword;

    @ApiModelProperty(value = "导入密码")
    @ExportField(index = 5, header = "导入密码", width = 16, wrapText = true)
    private String importPassword;

    @ApiModelProperty(value = "描述")
    @ExportField(index = 6, header = "描述", width = 35, wrapText = true)
    private String description;

    static {
        TypeStore.STORE.register(MachineProxyDO.class, MachineProxyExportDTO.class, p -> {
            MachineProxyExportDTO dto = new MachineProxyExportDTO();
            dto.setHost(p.getProxyHost());
            dto.setPort(p.getProxyPort());
            Optional.ofNullable(p.getProxyType())
                    .map(ProxyType::of)
                    .map(ProxyType::getLabel)
                    .ifPresent(dto::setProxyType);
            dto.setUsername(p.getProxyUsername());
            dto.setEncryptPassword(p.getProxyPassword());
            dto.setDescription(p.getDescription());
            return dto;
        });
    }

}
