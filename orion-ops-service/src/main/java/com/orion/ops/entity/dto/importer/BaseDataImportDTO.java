package com.orion.ops.entity.dto.importer;

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
     * 非法信息 非法操作
     */
    private String illegalMessage;

    /**
     * 数据id 更新操作
     */
    private Long id;

}
