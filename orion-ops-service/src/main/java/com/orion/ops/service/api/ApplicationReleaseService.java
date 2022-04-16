package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.request.ApplicationReleaseAuditRequest;
import com.orion.ops.entity.request.ApplicationReleaseRequest;
import com.orion.ops.entity.vo.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 发布单 服务类
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
public interface ApplicationReleaseService {

    /**
     * 发布列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<ApplicationReleaseListVO> getReleaseList(ApplicationReleaseRequest request);

    /**
     * 查询发布机器列表
     *
     * @param id id
     * @return rows
     */
    List<ApplicationReleaseMachineVO> getReleaseMachineList(Long id);

    /**
     * 发布详情
     *
     * @param request request
     * @return row
     */
    ApplicationReleaseDetailVO getReleaseDetail(ApplicationReleaseRequest request);

    /**
     * 获取发布机器详情
     *
     * @param releaseMachineId id
     * @return row
     */
    ApplicationReleaseMachineVO getReleaseMachineDetail(Long releaseMachineId);

    /**
     * 提交
     *
     * @param request request
     * @return id
     */
    Long submitAppRelease(ApplicationReleaseRequest request);

    /**
     * 复制
     *
     * @param id id
     * @return id
     */
    Long copyAppRelease(Long id);

    /**
     * 审核
     *
     * @param request request
     * @return effect
     */
    Integer auditAppRelease(ApplicationReleaseAuditRequest request);

    /**
     * 执行发布
     *
     * @param id             id
     * @param systemSchedule 是否为系统调度
     * @param execute        是否立即执行
     */
    void runnableAppRelease(Long id, boolean systemSchedule, boolean execute);

    /**
     * 取消执行
     *
     * @param id id
     */
    void cancelAppTimedRelease(Long id);

    /**
     * 设置定时时间
     *
     * @param id          id
     * @param releaseTime 定时时间
     */
    void setTimedRelease(Long id, Date releaseTime);

    /**
     * 回滚
     *
     * @param id id
     * @return id
     */
    Long rollbackAppRelease(Long id);

    /**
     * 终止
     *
     * @param id id
     */
    void terminatedRelease(Long id);

    /**
     * 终止
     *
     * @param id               id
     * @param releaseMachineId releaseMachineId
     */
    void terminatedMachine(Long id, Long releaseMachineId);

    /**
     * 跳过
     *
     * @param id               id
     * @param releaseMachineId releaseMachineId
     */
    void skipMachine(Long id, Long releaseMachineId);

    /**
     * 删除
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteRelease(List<Long> idList);

    /**
     * 获取发布状态列表
     *
     * @param idList        idList
     * @param machineIdList machineIdList
     * @return list
     */
    List<ApplicationReleaseStatusVO> getReleaseStatusList(List<Long> idList, List<Long> machineIdList);

    /**
     * 获取发布状态
     *
     * @param id id
     * @return status
     */
    ApplicationReleaseStatusVO getReleaseStatus(Long id);

    /**
     * 获取发布状态机器列表
     *
     * @param releaseMachineIdList releaseMachineIdList
     * @return list
     */
    List<ApplicationReleaseMachineStatusVO> getReleaseMachineStatusList(List<Long> releaseMachineIdList);

    /**
     * 获取发布机器状态
     *
     * @param releaseMachineId releaseMachineId
     * @return status
     */
    ApplicationReleaseMachineStatusVO getReleaseMachineStatus(Long releaseMachineId);

}
