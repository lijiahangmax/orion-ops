package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.IgnoreLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.WebSideMessageRequest;
import com.orion.ops.entity.vo.WebSideMessagePollVO;
import com.orion.ops.entity.vo.WebSideMessageVO;
import com.orion.ops.service.api.WebSideMessageService;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 站内信 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 11:27
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/message")
public class WebSideMessageController {

    @Resource
    private WebSideMessageService webSideMessageService;

    /**
     * 获取未读数量
     */
    @RequestMapping("/unread-count")
    public Integer getUnreadCount() {
        return webSideMessageService.getUnreadCount();
    }

    /**
     * 轮询站内信
     */
    @IgnoreLog
    @RequestMapping("/poll-message")
    public WebSideMessagePollVO pollMessage(@RequestBody WebSideMessageRequest request) {
        return webSideMessageService.pollMessage(request.getMaxId());
    }

    /**
     * 设置全部已读
     */
    @RequestMapping("/set-all-read")
    public Integer setAllRead() {
        return webSideMessageService.setAllRead();
    }

    /**
     * 站内信列表
     */
    @RequestMapping("/list")
    public DataGrid<WebSideMessageVO> getMessageList(@RequestBody WebSideMessageRequest request) {
        return webSideMessageService.getMessageList(request);
    }

    /**
     * 站内信详情
     */
    @RequestMapping("/detail")
    public WebSideMessageVO getMessageDetail(@RequestBody WebSideMessageRequest request) {
        Long id = Valid.notNull(request.getId());
        return webSideMessageService.getMessageDetail(id);
    }

    /**
     * 删除站内信
     */
    @RequestMapping("/delete")
    public Integer deleteMessage(@RequestBody WebSideMessageRequest request) {
        List<Long> idList = Valid.notNull(request.getIdList());
        return webSideMessageService.deleteMessage(idList);
    }

}
