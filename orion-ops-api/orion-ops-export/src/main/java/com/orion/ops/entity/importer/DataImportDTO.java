package com.orion.ops.entity.importer;

import com.orion.ops.constant.ImportType;
import com.orion.ops.entity.vo.data.DataImportCheckVO;
import lombok.Data;

import java.util.Date;

/**
 * 数据导入对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 17:42
 */
@Data
public class DataImportDTO {

    /**
     * token
     */
    private String importToken;

    /**
     * 类型
     *
     * @see ImportType
     */
    private Integer type;

    /**
     * 导入数据 json
     */
    private String data;

    /**
     * 检查数据
     */
    private DataImportCheckVO check;

    /**
     * 导入用户id 手动
     */
    private Long userId;

    /**
     * 导入用户名称 手动
     */
    private String userName;

    /**
     * 导入时间 手动
     */
    private Date importTime;

}
