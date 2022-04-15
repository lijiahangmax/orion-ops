package com.orion.ops.utils;

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
import com.orion.constant.Const;

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
                .setSwagger2(false)
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
                .setUrl("jdbc:mysql://localhost:3306/orion-ops?characterEncoding=utf8")
                // 配置账号
                .setUsername("root")
                // 配置密码
                .setPassword("admin123")
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
                .setInclude("application_pipeline_record_log");

        // 包名策略配置
        PackageConfig pkConfig = new PackageConfig()
                // 声明父包
                .setParent("com.orion.ops")
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
