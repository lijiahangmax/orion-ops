package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用分支信息响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 17:56
 */
@Data
@ApiModel(value = "应用分支信息响应")
@SuppressWarnings("ALL")
public class ApplicationRepositoryBranchVO {

    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * @see com.orion.ops.constant.Const#IS_DEFAULT
     */
    @ApiModelProperty(value = "是否为默认")
    private Integer isDefault;

}
