package com.orion.ops.controller;

import com.orion.ops.annotation.IgnoreLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.app.ApplicationActionLogRequest;
import com.orion.ops.entity.vo.app.ApplicationActionLogVO;
import com.orion.ops.service.api.ApplicationActionLogService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 应用操作日志api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/9 14:42
 */
@Api(tags = "应用操作日志")
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-action-log")
public class ApplicationActionLogController {

    @Resource
    private ApplicationActionLogService applicationActionLogService;

    @PostMapping("/detail")
    @ApiOperation(value = "获取日志详情")
    public ApplicationActionLogVO getActionDetail(@RequestBody ApplicationActionLogRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationActionLogService.getDetailById(id);
    }

    @IgnoreLog
    @PostMapping("/status")
    @ApiOperation(value = "获取日志状态")
    public ApplicationActionLogVO getActionStatus(@RequestBody ApplicationActionLogRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationActionLogService.getStatusById(id);
    }

}
