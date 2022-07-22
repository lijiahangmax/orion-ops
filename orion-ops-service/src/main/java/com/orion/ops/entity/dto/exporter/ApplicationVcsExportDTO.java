package com.orion.ops.entity.dto.exporter;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.ops.constant.CnConst;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.app.RepositoryAuthType;
import com.orion.ops.constant.app.RepositoryTokenType;
import com.orion.ops.entity.domain.ApplicationVcsDO;
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
public class ApplicationVcsExportDTO {

    @ApiModelProperty(value = "名称")
    @ExportField(index = 0, header = "名称", width = 20)
    private String name;

    @ApiModelProperty(value = "url")
    @ExportField(index = 1, header = "url", width = 40, wrapText = true)
    private String url;

    @ApiModelProperty(value = "认证方式(密码/令牌)")
    @ExportField(index = 2, header = "认证方式(密码/令牌)", width = 23, selectOptions = {CnConst.PASSWORD, CnConst.TOKEN})
    private String authType;

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
        TypeStore.STORE.register(ApplicationVcsDO.class, ApplicationVcsExportDTO.class, p -> {
            ApplicationVcsExportDTO dto = new ApplicationVcsExportDTO();
            dto.setName(p.getVcsName());
            dto.setUrl(p.getVscUrl());
            // 认证方式
            RepositoryAuthType authType = RepositoryAuthType.of(p.getVcsAuthType());
            if (authType != null) {
                dto.setAuthType(authType.getLabel());
            }
            // 令牌类型
            if (RepositoryAuthType.TOKEN.equals(authType)) {
                Optional.ofNullable(p.getVcsTokenType())
                        .map(RepositoryTokenType::of)
                        .map(RepositoryTokenType::getLabel)
                        .ifPresent(dto::setTokenType);
                if (RepositoryTokenType.GITEE.getLabel().equals(dto.getTokenType())) {
                    dto.setUsername(p.getVscUsername());
                }
                dto.setEncryptAuthValue(p.getVcsPrivateToken());
            } else {
                dto.setUsername(p.getVscUsername());
                dto.setEncryptAuthValue(p.getVcsPassword());
            }
            dto.setDescription(p.getVcsDescription());
            return dto;
        });
    }

}
