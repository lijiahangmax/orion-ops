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
package cn.orionsec.ops.config;

import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.PropertiesConst;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.orion.lang.utils.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * swagger 配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/6 14:22
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Profile({"dev"})
public class SwaggerConfig {

    @Value("${login.token.header}")
    private String loginTokenHeader;

    @Value("${expose.api.access.header}")
    private String accessTokenHeader;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.getApiInfo())
                .securitySchemes(this.getSecuritySchemes())
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.orionsec.ops"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 配置 api 信息
     *
     * @return api info
     */
    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("orion-ops restful API")
                .contact(new Contact(Const.ORION_AUTHOR, Const.ORION_GITEE, Const.ORION_EMAIL))
                .version(PropertiesConst.ORION_OPS_VERSION)
                .description("orion-ops api 管理")
                .build();
    }

    /**
     * 认证配置
     *
     * @return security scheme
     */
    private List<SecurityScheme> getSecuritySchemes() {
        ApiKey loginToken = new ApiKey(loginTokenHeader, loginTokenHeader, "header");
        ApiKey accessToken = new ApiKey(accessTokenHeader, accessTokenHeader, "header");
        return Lists.of(loginToken, accessToken);
    }

}
