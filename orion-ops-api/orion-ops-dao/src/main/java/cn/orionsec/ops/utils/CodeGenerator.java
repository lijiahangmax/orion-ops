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
package cn.orionsec.ops.utils;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.orion.lang.constant.Const;
import com.orion.lang.utils.ext.PropertiesExt;

import java.io.File;

/**
 * @author Jiahang Li
 */
public class CodeGenerator {

    public static void main(String[] args) {
        runGenerator();
    }

    /**
     * 代码生成器
     */
    private static void runGenerator() {
        // 获取配置文件
        File file = new File("orion-ops-api/orion-ops-web/src/main/resources/application-dev.properties");
        PropertiesExt ext = new PropertiesExt(file);
        String url = ext.getValue("spring.datasource.url");
        String username = ext.getValue("spring.datasource.username");
        String password = ext.getValue("spring.datasource.password");
        // 全局配置
        GlobalConfig gbConfig = new GlobalConfig()
                // 是否支持AR模式
                .setActiveRecord(false)
                // 设置作者
                .setAuthor(Const.ORION_AUTHOR)
                // 生成路径
                .setOutputDir("D:/MP/")
                // 文件是否覆盖
                .setFileOverride(true)
                // 主键策略
                .setIdType(IdType.AUTO)
                // Service名称
                .setServiceName("%sService")
                // 业务实现类名称
                .setServiceImplName("%sServiceImpl")
                // 实体对象名名称
                .setEntityName("%sDO")
                // 映射接口名称
                .setMapperName("%sDAO")
                // 映射文件名称
                .setXmlName("%sMapper")
                // web名称
                .setControllerName("%sController")
                // 生成 swagger2 注解
                .setSwagger2(true)
                // 开启 Kotlin 模式
                .setKotlin(false)
                // 是否生成ResultMap
                .setBaseResultMap(true)
                // 是否生成二级缓存
                .setEnableCache(false)
                // date类型
                .setDateType(DateType.ONLY_DATE)
                // 是否生成SQL片段
                .setBaseColumnList(true);

        // 数据源配置
        DataSourceConfig dsConfig = new DataSourceConfig()
                // 配置数据库类型
                .setDbType(DbType.MYSQL)
                // 配置驱动
                .setDriverName("com.mysql.cj.jdbc.Driver")
                // 配置路径
                .setUrl(url)
                // 配置账号
                .setUsername(username)
                // 配置密码
                .setPassword(password)
                // 转换器
                .setTypeConvert(new MySqlTypeConvert() {
                    @Override
                    public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                        if (fieldType.toLowerCase().contains("tinyint")) {
                            return DbColumnType.INTEGER;
                        }
                        return super.processTypeConvert(globalConfig, fieldType);
                    }
                });

        // 策略配置
        StrategyConfig stConfig = new StrategyConfig()
                // 全局大写命名
                .setCapitalMode(true)
                // 生成实体类注解
                .setEntityTableFieldAnnotationEnable(true)
                // 是否使用lombok写get set 方法
                .setEntityLombokModel(true)
                // 前端是否使用 @RestController
                .setRestControllerStyle(true)
                // 驼峰转连接字符
                .setControllerMappingHyphenStyle(false)
                // Boolean类型字段是否移除is前缀
                .setEntityBooleanColumnRemoveIsPrefix(false)
                // 下滑线转驼峰命名策略
                .setNaming(NamingStrategy.underline_to_camel)
                // 是否生成字段常量
                .setEntityColumnConstant(false)
                // 是否链式结构
                .setChainModel(false)
                // 配置表前缀
                .setTablePrefix("")
                // 配置字段前缀
                .setFieldPrefix("")
                // 生成的表
                .setInclude("machine_group", "machine_group_rel");

        // 包名策略配置
        PackageConfig pkConfig = new PackageConfig()
                // 声明父包
                .setParent("cn.orionsec.ops")
                // 映射接口的包
                .setMapper("dao")
                // service接口的包
                .setService("service")
                // serviceImpl接口的包
                .setServiceImpl("service.impl")
                // controller接口的包
                .setController("controller")
                // 实体类的包
                .setEntity("entity.domain")
                // 映射文件的包
                .setXml("mapper");

        // 整合配置
        AutoGenerator ag = new AutoGenerator()
                // 整合全局配置
                .setGlobalConfig(gbConfig)
                // 整合数据源配置
                .setDataSource(dsConfig)
                // 整合表名配置
                .setStrategy(stConfig)
                // 整合包名策略
                .setPackageInfo(pkConfig);

        // 执行
        ag.execute();
    }
}
