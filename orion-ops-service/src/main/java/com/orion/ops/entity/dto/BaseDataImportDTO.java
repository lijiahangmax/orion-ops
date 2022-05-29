package com.orion.ops.entity.dto;

import lombok.Data;

/**
 * 导入数据 基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 15:27
 */
@Data
public class BaseDataImportDTO {

    /**
     * 非法信息
     */
    private String illegalMessage;

}
