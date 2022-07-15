package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统站内信
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-03-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("web_side_message")
public class WebSideMessageDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息分类
     *
     * @see com.orion.ops.constant.message.MessageClassify
     */
    @TableField("message_classify")
    private Integer messageClassify;

    /**
     * 消息类型
     *
     * @see com.orion.ops.constant.message.MessageType
     */
    @TableField("message_type")
    private Integer messageType;

    /**
     * 是否已读 1未读 2已读
     *
     * @see com.orion.ops.constant.message.ReadStatus
     */
    @TableField("read_status")
    private Integer readStatus;

    /**
     * 收信人id
     */
    @TableField("to_user_id")
    private Long toUserId;

    /**
     * 收信人名称
     */
    @TableField("to_user_name")
    private String toUserName;

    /**
     * 消息
     */
    @TableField("send_message")
    private String sendMessage;

    /**
     * 参数
     */
    @TableField("params_json")
    private String paramsJson;

    /**
     * 是否删除 1未删除 2已删除
     *
     * @see com.orion.ops.constant.Const#NOT_DELETED
     * @see com.orion.ops.constant.Const#IS_DELETED
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

}
