package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.EventLogRequest;
import com.orion.ops.entity.vo.EventLogVO;
import com.orion.ops.service.api.UserEventLogService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 操作日志 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/10 16:17
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/log")
public class EventLogController {

    @Resource
    private UserEventLogService userEventLogService;

    /**
     * 获取操作日志
     */
    @RequestMapping("/list")
    public DataGrid<EventLogVO> getLogList(@RequestBody EventLogRequest request) {
        return userEventLogService.getLogList(request);
    }

}
