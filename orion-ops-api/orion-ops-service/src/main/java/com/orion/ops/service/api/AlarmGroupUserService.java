package com.orion.ops.service.api;

import com.orion.ops.entity.domain.AlarmGroupUserDO;

import java.util.List;

/**
 * 报警组员服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 10:29
 */
public interface AlarmGroupUserService {

    /**
     * 通过 groupId 查询
     *
     * @param groupId groupId
     * @return rows
     */
    List<AlarmGroupUserDO> selectByGroupId(Long groupId);

    /**
     * 通过 groupId 查询
     *
     * @param groupIdList groupIdList
     * @return rows
     */
    List<AlarmGroupUserDO> selectByGroupIdList(List<Long> groupIdList);

    /**
     * 通过 groupId 删除
     *
     * @param groupId groupId
     * @return effect
     */
    Integer deleteByGroupId(Long groupId);

    /**
     * 通过 userId 删除
     *
     * @param userId userId
     * @return effect
     */
    Integer deleteByUserId(Long userId);

}
