package com.orion.ops.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 历史值快照响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 19:03
 */
@Data
@ApiModel(value = "历史值快照响应")
@SuppressWarnings("ALL")
public class HistoryValueVO {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * @see com.orion.ops.constant.history.HistoryOperator
     */
    @ApiModelProperty(value = "操作类型 1新增 2修改 3删除")
    private Integer type;

    @ApiModelProperty(value = "原始值")
    private String beforeValue;

    @ApiModelProperty(value = "新值")
    private String afterValue;

    @ApiModelProperty(value = "修改人id")
    private Long updateUserId;

    @ApiModelProperty(value = "修改人用户名")
    private String updateUserName;

    @ApiModelProperty(value = "修改时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private String createTimeAgo;

}
