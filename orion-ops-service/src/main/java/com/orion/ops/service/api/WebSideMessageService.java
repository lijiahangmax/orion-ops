package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.request.WebSideMessageRequest;
import com.orion.ops.entity.vo.WebSideMessageVO;

import java.util.List;

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

}
