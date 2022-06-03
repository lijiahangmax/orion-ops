package com.orion.ops.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 数据导入检查
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/27 11:18
 */
@Data
public class DataImportCheckVO {

    /**
     * 无效行
     */
    private List<DataImportCheckRowVO> illegalRows;

    /**
     * 插入行
     */
    private List<DataImportCheckRowVO> insertRows;

    /**
     * 更新行
     */
    private List<DataImportCheckRowVO> updateRows;

    /**
     * 导入 token
     */
    private String importToken;

}
