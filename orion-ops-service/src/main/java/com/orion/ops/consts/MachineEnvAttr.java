package com.orion.ops.consts;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 机器环境变量key
 *
 * @author ljh15
 * @version 1.0.0
 * @since 2021/3/29 15:42
 */
@Getter
public enum MachineEnvAttr {

    /**
     * JAVA_BIN_PATH
     */
    JAVA_BIN_PATH(true, true, "java 命令路径"),

    /**
     * MAVEN_BIN_PATH
     */
    MAVEN_BIN_PATH(true, false, "maven 命令路径"),

    /**
     * NPM_BIN_PATH
     */
    NPM_BIN_PATH(true, false, "npm 命令路径"),

    /**
     * YARN_BIN_PATH
     */
    YARN_BIN_PATH(true, false, "yarn 命令路径"),

    /**
     * SVN_BIN_PATH
     */
    SVN_BIN_PATH(true, false, "svn 命令路径"),

    /**
     * GIT_BIN_PATH
     */
    GIT_BIN_PATH(true, false, "git 命令路径"),

    /**
     * 代码目录
     */
    CHECK_PATH(true, true, "代码目录"),

    /**
     * 操作日志目录
     */
    LOG_PATH(true, true, "操作日志目录"),

    /**
     * 存放秘钥文件目录
     */
    KEY_PATH(true, false, "存放秘钥文件目录"),

    /**
     * 宿主机存放部署产物目录
     * 目标集群产物分发的目录
     */
    DIST_PATH(true, true, "产物目录"),

    /**
     * nginx静态文件映射目录
     */
    NGINX_WWW_HOME(false, true, "nginx 静态文件映射目录"),

    /**
     * tomcat 根目录
     */
    TOMCAT_HOME(false, true, "tomcat 根目录"),

    ;

    /**
     * 宿主机是否需要
     */
    boolean host;

    /**
     * 目标机器是否需要
     */
    boolean target;

    /**
     * 描述
     */
    String description;

    @Setter
    String value;

    MachineEnvAttr(boolean host, boolean target, String description) {
        this.host = host;
        this.target = target;
        this.description = description;
    }

    public static List<String> getHostKeys() {
        return Arrays.stream(values())
                .filter(MachineEnvAttr::isHost)
                .map(MachineEnvAttr::name)
                .collect(Collectors.toList());
    }

    public static List<String> getTargetKeys() {
        return Arrays.stream(values())
                .filter(MachineEnvAttr::isTarget)
                .map(MachineEnvAttr::name)
                .collect(Collectors.toList());
    }

    public static MachineEnvAttr of(String key) {
        return Arrays.stream(values())
                .filter(a -> a.name().equals(key)).findFirst()
                .orElse(null);
    }

}
