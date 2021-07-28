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

    /**
     * 执行
     *
     * @throws Exception Exception
     */
    void handle() throws Exception;

    /**
     * 异常处理
     *
     * @param e e
     * @throws Exception throws
     */
    void onException(Exception e) throws Exception;

    /**
     * 跳过
     */
    void skip();

    /**
     * 是否执行成功
     *
     * @return 是否执行成功
     */
    boolean isSuccess();

    /**
     * 更新 Action
     *
     * @param id        id
     * @param status    status
     * @param startTime startTime
     * @param endTime   endTime
     */
    default void updateAction(Long id, ActionStatus status, Date startTime, Date endTime) {
        ReleaseActionDO update = new ReleaseActionDO();
        update.setId(id);
        update.setRunStatus(status.getStatus());
        update.setStartTime(startTime);
        update.setEndTime(endTime);
        SpringHolder.getBean(ReleaseActionDAO.class).updateById(update);
    }

}
