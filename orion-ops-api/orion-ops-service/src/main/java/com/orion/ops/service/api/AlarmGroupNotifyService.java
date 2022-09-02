package com.orion.ops.service.api;

import com.orion.ops.entity.domain.AlarmGroupNotifyDO;

import java.util.List;

/**
 * 报警组通知方式服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 10:29
 */
public interface AlarmGroupNotifyService {

    /**
     * 通过 groupId 查询
     *
     * @param groupId groupId
     * @return rows
     */
    List<AlarmGroupNotifyDO> selectByGroupId(Long groupId);

    /**
     * 通过 groupId 查询
     *
     * @param groupIdList groupIdList
     * @return rows
     */
    List<AlarmGroupNotifyDO> selectByGroupIdList(List<Long> groupIdList);

    /**
     * 通过 groupId 删除
     *
     * @param groupId groupId
     * @return effect
     */
    Integer deleteByGroupId(Long groupId);

    /**
     * 通过 webhookId 删除
     *
     * @param webhookId webhookId
     * @return effect
     */
    Integer deleteByWebhookId(Long webhookId);

}
