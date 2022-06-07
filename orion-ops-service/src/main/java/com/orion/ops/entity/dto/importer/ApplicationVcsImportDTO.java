package com.orion.ops.entity.dto.importer;

import com.orion.office.excel.annotation.ImportField;
import com.orion.ops.consts.app.VcsAuthType;
import com.orion.ops.consts.app.VcsTokenType;
import com.orion.ops.consts.app.VcsType;
import com.orion.ops.entity.domain.ApplicationVcsDO;
import com.orion.ops.entity.vo.DataImportCheckRowVO;
import com.orion.ops.utils.ValueMix;
import com.orion.utils.Strings;
import com.orion.utils.convert.TypeStore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * 应用仓库导入
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 15:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用仓库导入")
public class ApplicationVcsImportDTO extends BaseDataImportDTO {

    @ApiModelProperty(value = "名称")
    @ImportField(index = 0)
    private String name;

    @ApiModelProperty(value = "url")
    @ImportField(index = 1)
    private String url;

    @ApiModelProperty(value = "认证方式")
    @ImportField(index = 2)
    private String authType;

    @ApiModelProperty(value = "令牌类型")
    @ImportField(index = 3)
    private String tokenType;

    @ApiModelProperty(value = "用户名")
    @ImportField(index = 4)
    private String username;

    @ApiModelProperty(value = "导出密码/令牌 (密文)")
    @ImportField(index = 5)
    private String encryptAuthValue;

    @ApiModelProperty(value = "导入密码/令牌 (明文)")
    @ImportField(index = 6)
    private String importAuthValue;

    @ApiModelProperty(value = "描述")
    @ImportField(index = 7)
    private String description;

    @ApiModelProperty(value = "密码/令牌密文解密后的明文")
    private String decryptAuthValue;

    static {
        TypeStore.STORE.register(ApplicationVcsImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.name);
            vo.setIllegalMessage(p.getIllegalMessage());
            vo.setId(p.getId());
            return vo;
        });
        TypeStore.STORE.register(ApplicationVcsImportDTO.class, ApplicationVcsDO.class, p -> {
            ApplicationVcsDO d = new ApplicationVcsDO();
            d.setId(p.getId());
            d.setVcsName(p.name);
            d.setVcsDescription(p.description);
            d.setVcsType(VcsType.GIT.getType());
            d.setVscUrl(p.url);
            Optional.ofNullable(p.authType)
                    .map(VcsAuthType::of)
                    .map(VcsAuthType::getType)
                    .ifPresent(d::setVcsAuthType);
            if (VcsAuthType.PASSWORD.getType().equals(d.getVcsAuthType()) || VcsTokenType.GITEE.getLabel().equals(p.tokenType)) {
                d.setVscUsername(p.username);
            }
            Optional.ofNullable(p.tokenType)
                    .map(VcsTokenType::of)
                    .map(VcsTokenType::getType)
                    .ifPresent(d::setVcsTokenType);
            BiConsumer<ApplicationVcsDO, String> authValueSetter;
            if (VcsAuthType.PASSWORD.getType().equals(d.getVcsAuthType())) {
                authValueSetter = ApplicationVcsDO::setVcsPassword;
            } else {
                authValueSetter = ApplicationVcsDO::setVcsPrivateToken;
            }
            if (!Strings.isBlank(p.decryptAuthValue)) {
                authValueSetter.accept(d, ValueMix.encrypt(p.decryptAuthValue));
            }
            if (!Strings.isBlank(p.importAuthValue)) {
                authValueSetter.accept(d, ValueMix.encrypt(p.importAuthValue));
            }
            return d;
        });
    }

}
