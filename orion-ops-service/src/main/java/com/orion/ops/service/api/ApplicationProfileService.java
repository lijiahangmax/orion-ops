package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.entity.request.ApplicationProfileRequest;
import com.orion.ops.entity.vo.ApplicationProfileFastVO;
import com.orion.ops.entity.vo.ApplicationProfileVO;

import java.util.List;

/**
 * 应用服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 17:52
 */
public interface ApplicationProfileService {

    /**
     * 添加环境
     *
     * @param request request
     * @return id
     */
    Long addProfile(ApplicationProfileRequest request);

    /**
     * 更新环境
     *
     * @param request request
     * @return effect
     */
    Integer updateProfile(ApplicationProfileRequest request);

    /**
     * 删除环境
     *
     * @param id id
     * @return effect
     */
    Integer deleteProfile(Long id);

    /**
     * 环境列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<ApplicationProfileVO> listProfiles(ApplicationProfileRequest request);

    /**
     * 环境列表 (缓存)
     *
     * @return rows
     */
    List<ApplicationProfileFastVO> fastListProfiles();

    /**
     * 环境详情
     *
     * @param id id
     * @return rows
     */
    ApplicationProfileVO getProfile(Long id);

    /**
     * 清空环境缓存
     */
    void clearProfileCache();

}
