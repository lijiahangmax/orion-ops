package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.domain.ApplicationMachineDO;
import com.orion.ops.entity.request.ApplicationConfigRequest;
import com.orion.ops.entity.request.ApplicationInfoRequest;
import com.orion.ops.entity.vo.ApplicationDetailVO;
import com.orion.ops.entity.vo.ApplicationInfoVO;

import java.util.List;

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
     * 获取应用环境的机器
     *
     * @param appId     appId
     * @param profileId profileId
     * @return machineList
     */
    List<ApplicationMachineDO> getAppProfileMachine(Long appId, Long profileId);

    /**
     * 通过机器id删除应用机器
     *
     * @param machineId machineId
     * @return effect
     */
    Integer deleteAppMachineByMachineId(Long machineId);

    /**
     * 通过appId profileId删除应用机器
     *
     * @param appId     appId
     * @param profileId profileId
     * @return effect
     */
    Integer deleteAppMachineByAppProfileId(Long appId, Long profileId);

}
