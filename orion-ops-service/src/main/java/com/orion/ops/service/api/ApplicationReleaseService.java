package com.orion.ops.service.api;

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

}
