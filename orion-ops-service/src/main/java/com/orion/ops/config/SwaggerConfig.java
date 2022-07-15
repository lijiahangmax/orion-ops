package com.orion.ops.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.orion.lang.utils.collect.Lists;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.PropertiesConst;
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
@Profile({"dev", "local"})
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.getApiInfo())
                .securitySchemes(this.getSecuritySchemes())
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.orion.ops"))
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
        ApiKey loginToken = new ApiKey(PropertiesConst.LOGIN_TOKEN_HEADER, PropertiesConst.LOGIN_TOKEN_HEADER, "header");
        return Lists.of(loginToken);
    }

}
