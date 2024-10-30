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

import cn.orionsec.ops.constant.app.RepositoryAuthType;
import cn.orionsec.ops.constant.app.RepositoryTokenType;
import cn.orionsec.ops.constant.app.RepositoryType;
import cn.orionsec.ops.entity.domain.ApplicationRepositoryDO;
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
public class ApplicationRepositoryImportDTO extends BaseDataImportDTO {

    @ApiModelProperty(value = "名称")
    @ImportField(index = 0)
    private String name;

    @ApiModelProperty(value = "url")
    @ImportField(index = 1)
    private String url;

    /**
     * @see RepositoryAuthType
     */
    @ApiModelProperty(value = "认证方式")
    @ImportField(index = 2)
    private String authType;

    /**
     * @see RepositoryTokenType
     */
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
        TypeStore.STORE.register(ApplicationRepositoryImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.name);
            vo.setIllegalMessage(p.getIllegalMessage());
            vo.setId(p.getId());
            return vo;
        });
        TypeStore.STORE.register(ApplicationRepositoryImportDTO.class, ApplicationRepositoryDO.class, p -> {
            ApplicationRepositoryDO d = new ApplicationRepositoryDO();
            d.setId(p.getId());
            d.setRepoName(p.name);
            d.setRepoDescription(p.description);
            d.setRepoType(RepositoryType.GIT.getType());
            d.setRepoUrl(p.url);
            Optional.ofNullable(p.authType)
                    .map(RepositoryAuthType::of)
                    .map(RepositoryAuthType::getType)
                    .ifPresent(d::setRepoAuthType);
            if (RepositoryAuthType.PASSWORD.getType().equals(d.getRepoAuthType()) || RepositoryTokenType.GITEE.getLabel().equals(p.tokenType)) {
                d.setRepoUsername(p.username);
            }
            Optional.ofNullable(p.tokenType)
                    .map(RepositoryTokenType::of)
                    .map(RepositoryTokenType::getType)
                    .ifPresent(d::setRepoTokenType);
            BiConsumer<ApplicationRepositoryDO, String> authValueSetter;
            if (RepositoryAuthType.PASSWORD.getType().equals(d.getRepoAuthType())) {
                authValueSetter = ApplicationRepositoryDO::setRepoPassword;
            } else {
                authValueSetter = ApplicationRepositoryDO::setRepoPrivateToken;
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
