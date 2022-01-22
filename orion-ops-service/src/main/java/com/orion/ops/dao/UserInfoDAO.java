package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.UserInfoDO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-04-20
 */
public interface UserInfoDAO extends BaseMapper<UserInfoDO> {

    /**
     * 更新最后登录时间
     *
     * @param userId userId
     * @return effect
     */
    Integer updateLastLoginTime(@Param("userId") Long userId);

}
