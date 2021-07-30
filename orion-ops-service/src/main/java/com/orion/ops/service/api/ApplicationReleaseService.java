package com.orion.ops.service.api;

import com.orion.ops.entity.request.ApplicationReleaseAuditRequest;
import com.orion.ops.entity.request.ApplicationReleaseBillRequest;
import com.orion.ops.entity.request.ApplicationReleaseSubmitRequest;

/**
 * 应用上线单
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/10 20:33
 */
public interface ApplicationReleaseService {

    /**
     * 提交上线单
     *
     * @param request request
     * @return id
     */
    Long submitAppRelease(ApplicationReleaseSubmitRequest request);

    /**
     * 复制上线单
     *
     * @param id id
     * @return targetId
     */
    Long copyAppRelease(Long id);

    /**
     * 审核上线单
     *
     * @param request request
     * @return effect
     */
    Integer auditAppRelease(ApplicationReleaseAuditRequest request);

    /**
     * 执行上线单
     *
     * @param request request
     */
    void runnableAppRelease(ApplicationReleaseBillRequest request);

    /**
     * 回滚上线单
     *
     * @param id id
     * @return id
     */
    Long rollbackAppRelease(Long id);

}
