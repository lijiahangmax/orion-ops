package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.consts.message.MessageType;
import com.orion.ops.entity.request.WebSideMessageRequest;
import com.orion.ops.entity.vo.WebSideMessagePollVO;
import com.orion.ops.entity.vo.WebSideMessageVO;

import java.util.List;
import java.util.Map;

/**
 * 站内信服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 11:26
 */
public interface WebSideMessageService {

    /**
     * 获取未读数量
     *
     * @return 未读数量
     */
    Integer getUnreadCount();

    /**
     * 设置全部已读
     *
     * @return effect
     */
    Integer setAllRead();

    /**
     * 轮询站内信
     *
     * @param maxId maxId
     * @return 轮询返回
     */
    WebSideMessagePollVO pollMessage(Long maxId);

    /**
     * 站内信列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<WebSideMessageVO> getMessageList(WebSideMessageRequest request);

    /**
     * 站内信详情
     *
     * @param id id
     * @return message
     */
    WebSideMessageVO getMessageDetail(Long id);

    /**
     * 删除站内信
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteMessage(List<Long> idList);

    /**
     * 添加站内信 给当前用户
     *
     * @param type   type
     * @param params 参数
     */
    void addMessage(MessageType type, Map<String, Object> params);

    /**
     * 添加站内信
     *
     * @param type     type
     * @param userId   收信人 userId
     * @param username 收信人  username
     * @param params   参数
     */
    void addMessage(MessageType type, Long userId, String username, Map<String, Object> params);

}
