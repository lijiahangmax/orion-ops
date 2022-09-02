package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.annotation.IgnoreLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.message.WebSideMessageRequest;
import com.orion.ops.entity.vo.message.WebSideMessagePollVO;
import com.orion.ops.entity.vo.message.WebSideMessageVO;
import com.orion.ops.service.api.WebSideMessageService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/unread-count")
    @ApiOperation(value = "获取站内信未读数量")
    public Integer getUnreadCount() {
        return webSideMessageService.getUnreadCount();
    }

    @GetMapping("/set-all-read")
    @ApiOperation(value = "设置站内信全部已读")
    public Integer setAllRead() {
        return webSideMessageService.setAllRead();
    }

    @PostMapping("/read")
    @ApiOperation(value = "设置已读站内信")
    public Integer readMessage(@RequestBody WebSideMessageRequest request) {
        List<Long> idList = Valid.notNull(request.getIdList());
        return webSideMessageService.readMessage(idList);
    }

    @GetMapping("/delete-all-read")
    @ApiOperation(value = "删除全部已读站内信")
    public Integer deleteAllRead() {
        return webSideMessageService.deleteAllRead();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除站内信")
    public Integer deleteMessage(@RequestBody WebSideMessageRequest request) {
        List<Long> idList = Valid.notNull(request.getIdList());
        return webSideMessageService.deleteMessage(idList);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取站内信详情")
    public WebSideMessageVO getMessageDetail(@RequestBody WebSideMessageRequest request) {
        Long id = Valid.notNull(request.getId());
        return webSideMessageService.getMessageDetail(id);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取站内信列表")
    public DataGrid<WebSideMessageVO> getMessageList(@RequestBody WebSideMessageRequest request) {
        return webSideMessageService.getMessageList(request);
    }

    @PostMapping("/get-new-message")
    @ApiOperation(value = "获取最新站内信")
    public WebSideMessagePollVO getNewMessage(@RequestBody WebSideMessageRequest request) {
        return webSideMessageService.getNewMessage(request);
    }

    @PostMapping("/get-more-message")
    @ApiOperation(value = "获取更多站内信")
    public List<WebSideMessageVO> getMoreMessage(@RequestBody WebSideMessageRequest request) {
        Valid.notNull(request.getMaxId());
        return webSideMessageService.getMoreMessage(request);
    }

    @IgnoreLog
    @PostMapping("/poll-new-message")
    @ApiOperation(value = "轮询最新站内信")
    public WebSideMessagePollVO pollWebSideMessage(@RequestBody WebSideMessageRequest request) {
        return webSideMessageService.pollWebSideMessage(request.getMaxId());
    }

}
