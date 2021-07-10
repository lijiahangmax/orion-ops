package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.request.ApplicationConfigRequest;
import com.orion.ops.entity.request.ApplicationInfoRequest;
import com.orion.ops.entity.vo.ApplicationDetailVO;
import com.orion.ops.entity.vo.ApplicationInfoVO;

/**
 * 应用服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 17:52
 */
public interface ApplicationInfoService {

    /**
     * 添加应用
     *
     * @param request request
     * @return id
     */
    Long insertApp(ApplicationInfoRequest request);

    /**
     * 更新应用
     *
     * @param request request
     * @return effect
     */
    Integer updateApp(ApplicationInfoRequest request);

    /**
     * 更新排序
     *
     * @param id   id
     * @param incr 是否为上调
     * @return effect
     */
    Integer updateAppSort(Long id, boolean incr);

    /**
     * 删除应用
     *
     * @param id id
     * @return effect
     */
    Integer deleteApp(Long id);

    /**
     * 应用列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<ApplicationInfoVO> listApp(ApplicationInfoRequest request);

    /**
     * 获取应用详情
     *
     * @param appId     appId
     * @param profileId profileId
     * @return detail
     */
    ApplicationDetailVO getAppDetail(Long appId, Long profileId);

    /**
     * 配置app环境
     *
     * @param request request
     */
    void configAppProfile(ApplicationConfigRequest request);

    /**
     * 同步app环境
     *
     * @param appId         appId
     * @param profileId     profileId
     * @param syncProfileId 需要同步的profileId
     */
    void syncAppProfileConfig(Long appId, Long profileId, Long syncProfileId);

    /**
     * 复制app
     *
     * @param appId appId
     */
    void copyApplication(Long appId);

    /**
     * 检查app是否已经配置
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 检测app是否已经配置
     */
    boolean checkAppConfig(Long appId, Long profileId);

}
