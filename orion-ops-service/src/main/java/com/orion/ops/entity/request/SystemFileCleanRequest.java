package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 文件清理请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/17 23:43
 */
@Data
public class SystemFileCleanRequest {

    /**
     * 文件清理类型
     *
     * @see com.orion.ops.consts.system.SystemCleanType
     */
    private Integer cleanType;

}
