package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationMachineDO;

/**
 * <p>
 * 应用依赖机器表 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-07-02
 */
public interface ApplicationMachineDAO extends BaseMapper<ApplicationMachineDO> {

    /**NO_SUCH_FILE
     * 更新版本
     *
     * @param update update
     * @return effect
     */
    Integer updateAppVersion(ApplicationMachineDO update);

}
