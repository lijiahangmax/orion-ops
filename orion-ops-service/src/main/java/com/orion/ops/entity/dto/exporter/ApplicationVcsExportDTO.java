package com.orion.ops.entity.dto.exporter;

import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.ops.consts.CnConst;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.app.VcsAuthType;
import com.orion.ops.consts.app.VcsTokenType;
import com.orion.ops.entity.domain.ApplicationVcsDO;
import com.orion.utils.convert.TypeStore;
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
@ExportTitle(title = "应用仓库导出")
@ExportSheet(name = "应用仓库", titleHeight = 22, headerHeight = 22, freezeHeader = true, filterHeader = true)
public class ApplicationVcsExportDTO {

    /**
     * 名称
     */
    @ExportField(index = 0, header = "名称", width = 20)
    private String name;

    /**
     * url
     */
    @ExportField(index = 1, header = "url", width = 40, wrapText = true)
    private String url;

    /**
     * 认证方式
     */
    @ExportField(index = 2, header = "认证方式(密码/令牌)", width = 23, selectOptions = {CnConst.PASSWORD, CnConst.TOKEN})
    private String authType;

    /**
     * 令牌类型
     */
    @ExportField(index = 3, header = "令牌类型", width = 13, selectOptions = {Const.GITHUB, Const.GITEE, Const.GITLAB})
    private String tokenType;

    /**
     * 用户名
     */
    @ExportField(index = 4, header = "用户名", width = 20)
    private String username;

    /**
     * 导出密码/令牌 (密文)
     */
    @ExportField(index = 5, header = "导出密码/令牌", width = 21, hidden = true, wrapText = true)
    private String encryptAuthValue;

    /**
     * 导入密码/令牌 (明文)
     */
    @ExportField(index = 6, header = "导入密码/令牌", width = 21, wrapText = true)
    private String importAuthValue;

    /**
     * 描述
     */
    @ExportField(index = 7, header = "描述", width = 25, wrapText = true)
    private String description;

    static {
        TypeStore.STORE.register(ApplicationVcsDO.class, ApplicationVcsExportDTO.class, p -> {
            ApplicationVcsExportDTO dto = new ApplicationVcsExportDTO();
            dto.setName(p.getVcsName());
            dto.setUrl(p.getVscUrl());
            // 认证方式
            VcsAuthType authType = VcsAuthType.of(p.getVcsAuthType());
            if (authType != null) {
                dto.setAuthType(authType.getLabel());
            }
            // 令牌类型
            if (VcsAuthType.TOKEN.equals(authType)) {
                Optional.ofNullable(p.getVcsTokenType())
                        .map(VcsTokenType::of)
                        .map(VcsTokenType::getLabel)
                        .ifPresent(dto::setTokenType);
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
