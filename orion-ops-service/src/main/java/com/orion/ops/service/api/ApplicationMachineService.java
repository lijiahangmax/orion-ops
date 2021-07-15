package com.orion.ops.service.api;

import com.orion.ops.entity.vo.ApplicationMachineVO;

import java.util.List;

/**
 * 应用机器服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/9 18:24
 */
public interface ApplicationMachineService {

    /**
     * 获取应用环境的机器
     *
     * @param machineId machineId
     * @param appId     appId
     * @param profileId profileId
     * @return machineId
     */
    Long getAppProfileMachineId(Long machineId, Long appId, Long profileId);

    /**
     * 获取应用环境的机器id
     *
     * @param appId     appId
     * @param profileId profileId
     * @return machineIdList
     */
    List<Long> getAppProfileMachineIdList(Long appId, Long profileId);

    /**
     * 获取应用环境的机器
     *
     * @param appId     appId
     * @param profileId profileId
     * @return machineList
     */
    List<ApplicationMachineVO> getAppProfileMachineList(Long appId, Long profileId);

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

    /**
     * 通过appId profileId查询应用机器数量
     *
     * @param appId     appId
     * @param profileId profileId
     * @return count
     */
    Integer selectAppProfileMachineCount(Long appId, Long profileId);

    /**
     * 同步app机器
     *
     * @param appId         appId
     * @param profileId     profileId
     * @param syncProfileId 需要同步的profileId
     */
    void syncAppProfileMachine(Long appId, Long profileId, Long syncProfileId);

    /**
     * 复制app机器
     *
     * @param appId       appId
     * @param targetAppId targetAppId
     */
    void copyAppMachine(Long appId, Long targetAppId);

}
