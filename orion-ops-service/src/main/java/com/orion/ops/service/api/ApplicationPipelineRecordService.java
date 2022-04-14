package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.request.ApplicationPipelineRecordRequest;
import com.orion.ops.entity.vo.ApplicationPipelineRecordListVO;

import java.util.Date;
import java.util.List;

/**
 * 应用流水线明细服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:55
 */
public interface ApplicationPipelineRecordService {

    /**
     * 获取列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<ApplicationPipelineRecordListVO> getPipelineRecordList(ApplicationPipelineRecordRequest request);

    /**
     * 提交流水线执行
     *
     * @param request request
     * @return id
     */
    Long submitPipelineExec(ApplicationPipelineRecordRequest request);

    /**
     * 审核流水线
     *
     * @param request request
     * @return effect
     */
    Integer auditPipeline(ApplicationPipelineRecordRequest request);

    /**
     * 复制流水线
     *
     * @param id id
     * @return effect
     */
    Long copyPipeline(Long id);

    /**
     * 执行流水线
     *
     * @param id       id
     * @param isSystem 是否为系统执行
     */
    void execPipeline(Long id, boolean isSystem);

    /**
     * 删除流水线
     *
     * @param idList idList
     * @return effect
     */
    Integer deletePipeline(List<Long> idList);

    /**
     * 设置定时执行流水线
     *
     * @param id            id
     * @param timedExecDate timedExecDate
     */
    void setPipelineTimedExec(Long id, Date timedExecDate);

    /**
     * 取消定时执行流水线
     *
     * @param id id
     */
    void cancelPipelineTimedExec(Long id);

    /**
     * 停止执行流水线
     *
     * @param id id
     */
    void terminatedExec(Long id);

}
