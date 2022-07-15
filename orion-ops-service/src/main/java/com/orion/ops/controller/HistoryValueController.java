package com.orion.ops.controller;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.history.HistoryValueType;
import com.orion.ops.entity.request.HistoryValueRequest;
import com.orion.ops.entity.vo.HistoryValueVO;
import com.orion.ops.service.api.HistoryValueService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 历史值 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 17:15
 */
@Api(tags = "历史值")
@RestController
@RestWrapper
@RequestMapping("/orion/api/history-value")
public class HistoryValueController {

    @Resource
    private HistoryValueService historyValueService;

    @PostMapping("/list")
    @ApiOperation(value = "历史值列表")
    public DataGrid<HistoryValueVO> list(@RequestBody HistoryValueRequest request) {
        Valid.notNull(request.getValueId());
        Valid.notNull(HistoryValueType.of(request.getValueType()));
        return historyValueService.list(request);
    }

    @PostMapping("/rollback")
    @ApiOperation(value = "回滚历史值")
    public HttpWrapper<?> rollback(@RequestBody HistoryValueRequest request) {
        Long id = Valid.notNull(request.getId());
        historyValueService.rollback(id);
        return HttpWrapper.ok();
    }

}
