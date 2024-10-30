/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.controller;

import cn.orionsec.ops.annotation.IgnoreLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.entity.request.message.WebSideMessageRequest;
import cn.orionsec.ops.entity.vo.message.WebSideMessagePollVO;
import cn.orionsec.ops.entity.vo.message.WebSideMessageVO;
import cn.orionsec.ops.service.api.WebSideMessageService;
import cn.orionsec.ops.utils.Valid;
import com.orion.lang.define.wrapper.DataGrid;
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
