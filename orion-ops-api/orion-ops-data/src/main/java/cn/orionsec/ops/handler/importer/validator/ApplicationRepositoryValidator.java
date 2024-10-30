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
package cn.orionsec.ops.handler.importer.validator;

import cn.orionsec.ops.constant.app.RepositoryAuthType;
import cn.orionsec.ops.constant.app.RepositoryTokenType;
import cn.orionsec.ops.entity.importer.ApplicationRepositoryImportDTO;
import cn.orionsec.ops.utils.Valid;
import cn.orionsec.ops.utils.ValueMix;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 应用版本仓库表 数据验证器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/2 10:02
 */
public class ApplicationRepositoryValidator implements DataValidator {

    private ApplicationRepositoryValidator() {
    }

    public static final ApplicationRepositoryValidator INSTANCE = new ApplicationRepositoryValidator();

    public static final int NAME_MAX_LEN = 32;

    public static final int URL_MAX_LEN = 1024;

    public static final int USERNAME_MAX_LEN = 128;

    public static final int TOKEN_MAX_LEN = 256;

    public static final int PASSWORD_MAX_LEN = 128;

    public static final int DESCRIPTION_MAX_LEN = 64;

    public static final String NAME_EMPTY_MESSAGE = "名称不能为空";

    public static final String NAME_LEN_MESSAGE = "名称长度不能大于 " + NAME_MAX_LEN + "位";

    public static final String URL_EMPTY_MESSAGE = "url 不能为空";

    public static final String URL_LEN_MESSAGE = "url 长度不能大于 " + URL_MAX_LEN + "位";

    public static final String AUTH_TYPE_EMPTY_MESSAGE = "认证方式不能为空";

    public static final String AUTH_TYPE_VALUE_MESSAGE = "认证方式只能为 " + Arrays.stream(RepositoryAuthType.values()).map(RepositoryAuthType::getLabel).collect(Collectors.toList());

    public static final String TOKEN_TYPE_EMPTY_MESSAGE = "认证方式为" + RepositoryAuthType.TOKEN.getLabel() + "时 令牌类型不能为空";

    public static final String TOKEN_TYPE_VALUE_MESSAGE = "令牌类型只能为 " + Arrays.stream(RepositoryTokenType.values()).map(RepositoryTokenType::getLabel).collect(Collectors.toList());

    public static final String PASSWORD_TOKEN_DECRYPT_MESSAGE = "密码/令牌密文解密失败";

    public static final String USERNAME_PASSWORD_MESSAGE = "用户名和密码必须同时存在";

    public static final String TOKEN_EMPTY_MESSAGE = "令牌不能为空";

    public static final String USERNAME_TOKEN_MESSAGE = "令牌类型为 " + RepositoryTokenType.GITEE.getLabel() + " 时 用户名和密钥必须同时存在";

    public static final String USERNAME_LEN_MESSAGE = "用户名长度不能大于 " + USERNAME_MAX_LEN + "位";

    public static final String TOKEN_LEN_MESSAGE = "密钥长度不能大于 " + TOKEN_MAX_LEN + "位";

    public static final String PASSWORD_LEN_MESSAGE = "密码长度不能大于 " + PASSWORD_MAX_LEN + "位";

    public static final String DESCRIPTION_LEN_MESSAGE = "描述长度不能大于 " + DESCRIPTION_MAX_LEN + "位";

    @Override
    public void validData(Object o) {
        if (o instanceof ApplicationRepositoryImportDTO) {
            validImport((ApplicationRepositoryImportDTO) o);
        }
    }

    /**
     * 验证导入数据
     *
     * @param row row
     */
    private void validImport(ApplicationRepositoryImportDTO row) {
        String name = row.getName();
        String url = row.getUrl();
        String authTypeValue = row.getAuthType();
        String tokenTypeValue = row.getTokenType();
        String username = row.getUsername();
        String encryptAuthValue = row.getEncryptAuthValue();
        String importAuthValue = row.getImportAuthValue();
        String description = row.getDescription();
        Valid.notBlank(name, NAME_EMPTY_MESSAGE);
        Valid.validLengthLte(name, NAME_MAX_LEN, NAME_LEN_MESSAGE);
        Valid.notBlank(url, URL_EMPTY_MESSAGE);
        Valid.validLengthLte(url, URL_MAX_LEN, URL_LEN_MESSAGE);
        Valid.notBlank(authTypeValue, AUTH_TYPE_EMPTY_MESSAGE);
        RepositoryAuthType authType = RepositoryAuthType.of(authTypeValue);
        Valid.notNull(authType, AUTH_TYPE_VALUE_MESSAGE);
        if (!Strings.isEmpty(encryptAuthValue)) {
            String decryptValue = ValueMix.decrypt(encryptAuthValue);
            Valid.notNull(decryptValue, PASSWORD_TOKEN_DECRYPT_MESSAGE);
            row.setDecryptAuthValue(decryptValue);
        }
        if (RepositoryAuthType.PASSWORD.equals(authType)) {
            // 密码验证
            if (!Strings.isEmpty(importAuthValue)) {
                Valid.validLengthLte(importAuthValue, PASSWORD_MAX_LEN, PASSWORD_LEN_MESSAGE);
            }
            if (Strings.isEmpty(username) ^ (Strings.isEmpty(row.getDecryptAuthValue()) && Strings.isEmpty(importAuthValue))) {
                throw Exceptions.argument(USERNAME_PASSWORD_MESSAGE);
            }
        } else {
            // 令牌验证
            Valid.notBlank(tokenTypeValue, TOKEN_TYPE_EMPTY_MESSAGE);
            RepositoryTokenType tokenType = RepositoryTokenType.of(tokenTypeValue);
            Valid.notNull(tokenType, TOKEN_TYPE_VALUE_MESSAGE);
            if (!Strings.isEmpty(importAuthValue)) {
                Valid.validLengthLte(importAuthValue, TOKEN_MAX_LEN, TOKEN_LEN_MESSAGE);
            }
            Valid.isTrue(!Strings.isBlank(importAuthValue) || !Strings.isBlank(row.getDecryptAuthValue()), TOKEN_EMPTY_MESSAGE);
            if (RepositoryTokenType.GITEE.equals(tokenType)) {
                Valid.notBlank(username, USERNAME_TOKEN_MESSAGE);
                Valid.validLengthLte(username, USERNAME_MAX_LEN, USERNAME_LEN_MESSAGE);
            }
        }
        if (!Strings.isBlank(description)) {
            Valid.validLengthLte(description, DESCRIPTION_MAX_LEN, DESCRIPTION_LEN_MESSAGE);
        }
    }

}
