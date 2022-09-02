package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.entity.request.alarm.AlarmGroupRequest;
import com.orion.ops.entity.vo.alarm.AlarmGroupVO;

/**
 * 报警组服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/25 15:37
 */
public interface AlarmGroupService {

    /**
     * 添加报警组
     *
     * @param request request
     * @return id
     */
    Long addAlarmGroup(AlarmGroupRequest request);

    /**
     * 更新报警组
     *
     * @param request request
     * @return effect
     */
    Integer updateAlarmGroup(AlarmGroupRequest request);

    /**
     * 删除报警组
     *
     * @param id id
     * @return effect
     */
    Integer deleteAlarmGroup(Long id);

    /**
     * 查询列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<AlarmGroupVO> getAlarmGroupList(AlarmGroupRequest request);

    /**
     * 查询详情
     *
     * @param id id
     * @return row
     */
    AlarmGroupVO getAlarmGroupDetail(Long id);

}
