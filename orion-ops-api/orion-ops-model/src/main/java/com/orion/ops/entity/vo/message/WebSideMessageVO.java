package com.orion.ops.entity.vo.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 站内信响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 16:25
 */
@Data
@ApiModel(value = "站内信响应")
@SuppressWarnings("ALL")
public class WebSideMessageVO {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * @see com.orion.ops.constant.message.MessageClassify
     */
    @ApiModelProperty(value = "消息分类")
    private Integer classify;

    /**
     * @see com.orion.ops.constant.message.MessageType
     */
    @ApiModelProperty(value = "消息类型")
    private Integer type;

    /**
     * @see com.orion.ops.constant.message.ReadStatus
     */
    @ApiModelProperty(value = "是否已读 1未读 2已读")
    private Integer status;

    @ApiModelProperty(value = "收信人id")
    private Long toUserId;

    @ApiModelProperty(value = "收信人名称")
    private String toUserName;

    @ApiModelProperty(value = "消息")
    private String message;

    @ApiModelProperty(value = "消息关联id")
    private Long relId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建时间")
    private String createTimeAgo;

}
