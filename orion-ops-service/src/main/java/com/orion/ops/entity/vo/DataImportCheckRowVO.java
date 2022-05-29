package com.orion.ops.entity.vo;

import lombok.Data;

/**
 * 数据导入检查
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/27 11:18
 */
@Data
public class DataImportCheckRowVO {

    /**
     * 索引 0开始
     */
    private Integer index;

    /**
     * 行号 前端提示
     */
    private Integer row;

    /**
     * 唯一标识
     */
    private String symbol;

    /**
     * 非法信息
     */
    private String illegalMessage;

}
