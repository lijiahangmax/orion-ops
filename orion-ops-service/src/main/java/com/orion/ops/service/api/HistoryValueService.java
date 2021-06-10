package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.HistoryValueType;
import com.orion.ops.entity.request.HistoryValueRequest;
import com.orion.ops.entity.vo.HistoryValueVO;

/**
 * 历史值 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 17:17
 */
public interface HistoryValueService {

    /**
     * 添加历史值快照
     *
     * @param valueId    valueId
     * @param valueType  valueType
     * @param beforeValue beforeValue
     */
    void addHistory(Long valueId, HistoryValueType valueType, String beforeValue);

    /**
     * 值列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<HistoryValueVO> list(HistoryValueRequest request);

    /**
     * 回滚
     *
     * @param id id
     * @return effect
     */
    Integer rollback(Long id);

}
