package com.orion.ops.entity.vo.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 数据导入检查响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/27 11:18
 */
@Data
@ApiModel(value = "数据导入检查响应")
public class DataImportCheckVO {

    @ApiModelProperty(value = "无效行")
    private List<DataImportCheckRowVO> illegalRows;

    @ApiModelProperty(value = "插入行")
    private List<DataImportCheckRowVO> insertRows;

    @ApiModelProperty(value = "更新行")
    private List<DataImportCheckRowVO> updateRows;

    @ApiModelProperty(value = "导入token")
    private String importToken;

}
