package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 数据导入请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 17:33
 */
@Data
public class DataImportRequest {

    /**
     * 导入 token
     */
    private String importToken;

}
