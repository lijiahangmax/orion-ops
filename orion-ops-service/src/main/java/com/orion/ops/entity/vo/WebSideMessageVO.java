package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.WebSideMessageDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;

/**
 * 站内信 vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 16:25
 */
@Data
public class WebSideMessageVO {

    /**
     * id
     */
    private Long id;

    /**
     * 消息分类
     *
     * @see com.orion.ops.consts.message.MessageClassify
     */
    private Integer classify;

    /**
     * 消息类型
     *
     * @see com.orion.ops.consts.message.MessageType
     */
    private Integer type;

    /**
     * 是否已读 1未读 2已读
     *
     * @see com.orion.ops.consts.message.ReadStatus
     */
    private Integer status;

    /**
     * 收信人id
     */
    private Long toUserId;

    /**
     * 收信人名称
     */
    private String toUserName;

    /**
     * 消息
     */
    private String message;

    /**
     * 参数
     */
    private String paramsJson;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建时间
     */
    private String createTimeAgo;

    static {
        TypeStore.STORE.register(WebSideMessageDO.class, WebSideMessageVO.class, p -> {
            WebSideMessageVO vo = new WebSideMessageVO();
            vo.setId(p.getId());
            vo.setClassify(p.getMessageClassify());
            vo.setType(p.getMessageType());
            vo.setStatus(p.getReadStatus());
            vo.setToUserId(p.getToUserId());
            vo.setToUserName(p.getToUserName());
            vo.setMessage(p.getSendMessage());
            vo.setParamsJson(p.getParamsJson());
            vo.setCreateTime(p.getCreateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            return vo;
        });
    }

}
