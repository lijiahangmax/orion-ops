package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统站内信
 *
 * @author Jiahang Li
 * @since 2022-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "系统站内信")
@TableName("web_side_message")
public class WebSideMessageDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * @see com.orion.ops.constant.message.MessageClassify
     */
    @ApiModelProperty(value = "消息分类")
    @TableField("message_classify")
    private Integer messageClassify;

    /**
     * @see com.orion.ops.constant.message.MessageType
     */
    @ApiModelProperty(value = "消息类型")
    @TableField("message_type")
    private Integer messageType;

    /**
     * @see com.orion.ops.constant.message.ReadStatus
     */
    @ApiModelProperty(value = "是否已读 1未读 2已读")
    @TableField("read_status")
    private Integer readStatus;

    @ApiModelProperty(value = "收信人id")
    @TableField("to_user_id")
    private Long toUserId;

    @ApiModelProperty(value = "收信人名称")
    @TableField("to_user_name")
    private String toUserName;

    @ApiModelProperty(value = "消息")
    @TableField("send_message")
    private String sendMessage;

    @ApiModelProperty(value = "参数")
    @TableField("params_json")
    private String paramsJson;

    /**
     * @see com.orion.ops.constant.Const#NOT_DELETED
     * @see com.orion.ops.constant.Const#IS_DELETED
     */
    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
