package com.orion.ops.entity.request;

import lombok.Data;

/**
 * app配置环境请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/5 18:50
 */
@Data
public class ApplicationConfigEnvRequest {

    /**
     * 版本控制根目录
     */
    private String vcsRootPath;

    /**
     * 应用代码目录
     */
    private String vcsCodePath;

    /**
     * 版本管理工具
     *
     * @see com.orion.ops.consts.app.VcsType
     */
    private String vcsType;

    /**
     * 构建产物目录
     */
    private String distPath;

}
