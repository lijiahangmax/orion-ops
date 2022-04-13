package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.request.ApplicationPipelineRecordRequest;
import com.orion.ops.entity.vo.ApplicationPipelineRecordListVO;

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


}
