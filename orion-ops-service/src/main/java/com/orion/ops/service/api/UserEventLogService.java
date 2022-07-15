package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.entity.request.EventLogRequest;
import com.orion.ops.entity.vo.EventLogVO;

/**
 * 用户操作日志服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/22 20:19
 */
public interface UserEventLogService {

    /**
     * 记录日志
     *
     * @param eventType 操作
     * @param isSuccess 是否成功
     */
    void recordLog(EventType eventType, boolean isSuccess);

    /**
     * 获取操作日志
     *
     * @param request request
     * @return rows
     */
    DataGrid<EventLogVO> getLogList(EventLogRequest request);

}
