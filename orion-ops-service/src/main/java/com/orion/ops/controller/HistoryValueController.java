package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.HistoryValueType;
import com.orion.ops.entity.request.HistoryValueRequest;
import com.orion.ops.entity.vo.HistoryValueVO;
import com.orion.ops.service.api.HistoryValueService;
import com.orion.ops.utils.Valid;
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
@RestController
@RestWrapper
@RequestMapping("/orion/api/history-value")
public class HistoryValueController {

    @Resource
    private HistoryValueService historyValueService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<HistoryValueVO> list(@RequestBody HistoryValueRequest request) {
        Valid.notNull(request.getValueId());
        Valid.notNull(HistoryValueType.of(request.getValueType()));
        return historyValueService.list(request);
    }

    /**
     * 回滚
     */
    @RequestMapping("/rollback")
    public Integer rollback(@RequestBody HistoryValueRequest request) {
        Long id = Valid.notNull(request.getId());
        return historyValueService.rollback(id);
    }

}
