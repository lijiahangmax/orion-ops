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
package cn.orionsec.ops.entity.exporter;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.office.excel.annotation.ExportField;
import cn.orionsec.kit.office.excel.annotation.ExportSheet;
import cn.orionsec.kit.office.excel.annotation.ExportTitle;
import cn.orionsec.ops.constant.CnConst;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.app.RepositoryAuthType;
import cn.orionsec.ops.constant.app.RepositoryTokenType;
import cn.orionsec.ops.entity.domain.ApplicationRepositoryDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Optional;

/**
 * 应用仓库导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 15:08
 */
@Data
@ApiModel(value = "应用仓库导出")
@ExportTitle(title = "应用仓库导出")
@ExportSheet(name = "应用仓库", titleHeight = 22, headerHeight = 22, freezeHeader = true, filterHeader = true)
public class ApplicationRepositoryExportDTO {

    @ApiModelProperty(value = "名称")
    @ExportField(index = 0, header = "名称", width = 20)
    private String name;

    @ApiModelProperty(value = "url")
    @ExportField(index = 1, header = "url", width = 40, wrapText = true)
    private String url;

    /**
     * @see RepositoryAuthType
     */
    @ApiModelProperty(value = "认证方式(密码/令牌)")
    @ExportField(index = 2, header = "认证方式(密码/令牌)", width = 23, selectOptions = {CnConst.PASSWORD, CnConst.TOKEN})
    private String authType;

    /**
     * @see RepositoryTokenType
     */
    @ApiModelProperty(value = "令牌类型")
    @ExportField(index = 3, header = "令牌类型", width = 13, selectOptions = {Const.GITHUB, Const.GITEE, Const.GITLAB})
    private String tokenType;

    @ApiModelProperty(value = "用户名")
    @ExportField(index = 4, header = "用户名", width = 20)
    private String username;

    @ApiModelProperty(value = "导出密码/令牌 (密文)")
    @ExportField(index = 5, header = "导出密码/令牌", width = 21, hidden = true, wrapText = true)
    private String encryptAuthValue;

    @ApiModelProperty(value = "导入密码/令牌 (明文)")
    @ExportField(index = 6, header = "导入密码/令牌", width = 21, wrapText = true)
    private String importAuthValue;

    @ApiModelProperty(value = "描述")
    @ExportField(index = 7, header = "描述", width = 25, wrapText = true)
    private String description;

    static {
        TypeStore.STORE.register(ApplicationRepositoryDO.class, ApplicationRepositoryExportDTO.class, p -> {
            ApplicationRepositoryExportDTO dto = new ApplicationRepositoryExportDTO();
            dto.setName(p.getRepoName());
            dto.setUrl(p.getRepoUrl());
            // 认证方式
            RepositoryAuthType authType = RepositoryAuthType.of(p.getRepoAuthType());
            if (authType != null) {
                dto.setAuthType(authType.getLabel());
            }
            // 令牌类型
            if (RepositoryAuthType.TOKEN.equals(authType)) {
                Optional.ofNullable(p.getRepoTokenType())
                        .map(RepositoryTokenType::of)
                        .map(RepositoryTokenType::getLabel)
                        .ifPresent(dto::setTokenType);
                if (RepositoryTokenType.GITEE.getLabel().equals(dto.getTokenType())) {
                    dto.setUsername(p.getRepoUsername());
                }
                dto.setEncryptAuthValue(p.getRepoPrivateToken());
            } else {
                dto.setUsername(p.getRepoUsername());
                dto.setEncryptAuthValue(p.getRepoPassword());
            }
            dto.setDescription(p.getRepoDescription());
            return dto;
        });
    }

}
