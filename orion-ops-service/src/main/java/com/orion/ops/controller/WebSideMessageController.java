package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.IgnoreLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.WebSideMessageRequest;
import com.orion.ops.entity.vo.WebSideMessagePollVO;
import com.orion.ops.entity.vo.WebSideMessageVO;
import com.orion.ops.service.api.WebSideMessageService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
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
@Api(tags = "站内信")
@RestController
@RestWrapper
@RequestMapping("/orion/api/message")
public class WebSideMessageController {

    @Resource
    private WebSideMessageService webSideMessageService;

    @PostMapping("/unread-count")
    @ApiOperation(value = "获取站内信未读数量")
    public Integer getUnreadCount() {
        return webSideMessageService.getUnreadCount();
    }

    @IgnoreLog
    @PostMapping("/poll-message")
    @ApiOperation(value = "轮询站内信")
    public WebSideMessagePollVO pollMessage(@RequestBody WebSideMessageRequest request) {
        return webSideMessageService.pollMessage(request.getMaxId());
    }

    @PostMapping("/set-all-read")
    @ApiOperation(value = "设置站内信全部已读")
    public Integer setAllRead() {
        return webSideMessageService.setAllRead();
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取站内信列表")
    public DataGrid<WebSideMessageVO> getMessageList(@RequestBody WebSideMessageRequest request) {
        return webSideMessageService.getMessageList(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取站内信详情")
    public WebSideMessageVO getMessageDetail(@RequestBody WebSideMessageRequest request) {
        Long id = Valid.notNull(request.getId());
        return webSideMessageService.getMessageDetail(id);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除站内信")
    public Integer deleteMessage(@RequestBody WebSideMessageRequest request) {
        List<Long> idList = Valid.notNull(request.getIdList());
        return webSideMessageService.deleteMessage(idList);
    }

}
