package com.orion.ops.handler.release.action;

import com.orion.able.SafeCloseable;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.dao.ReleaseActionDAO;
import com.orion.ops.entity.domain.ReleaseActionDO;
import com.orion.spring.SpringHolder;

import java.util.Date;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/15 17:22
 */
public interface IReleaseActionHandler extends SafeCloseable {

    ReleaseActionDAO RELEASE_ACTION_DAO = SpringHolder.getBean("releaseActionDAO");

    /**
     * 执行
     */
    void handle();

    /**
     * 异常处理
     *
     * @param e e
     */
    void onException(Exception e);

    /**
     * 是否已处理
     *
     * @return 是否已处理
     */
    boolean isHandled();

    /**
     * 修改actionStatus
     *
     * @param id        id
     * @param status    status
     * @param startTime startTime
     * @param endTime   endTime
     */
    default void updateActionStatus(Long id, ActionStatus status, Date startTime, Date endTime) {
        ReleaseActionDO update = new ReleaseActionDO();
        update.setId(id);
        update.setRunStatus(status.getStatus());
        update.setStartTime(startTime);
        update.setEndTime(endTime);
        RELEASE_ACTION_DAO.updateById(update);
    }

    /**
     * 修改action
     *
     * @param entity action
     */
    default void updateAction(ReleaseActionDO entity) {
        RELEASE_ACTION_DAO.updateById(entity);
    }

}
