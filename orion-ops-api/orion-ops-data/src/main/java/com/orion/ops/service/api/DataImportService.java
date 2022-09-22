package com.orion.ops.service.api;

import com.orion.ops.entity.importer.DataImportDTO;

/**
 * 数据导入服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 17:06
 */
public interface DataImportService {

    /**
     * 检查导入 token
     *
     * @param token token
     * @return 导入数据
     */
    DataImportDTO checkImportToken(String token);

    /**
     * 清空导入 token
     *
     * @param token token
     */
    void clearImportToken(String token);

}
