package com.orion.ops.handler.release.machine;

import com.beust.jcommander.internal.Maps;
import com.orion.ops.entity.domain.ApplicationReleaseActionDO;
import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;
import com.orion.ops.handler.release.handler.IReleaseHandler;
import com.orion.remote.channel.SessionStore;
import lombok.Data;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 发布机器信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/28 17:39
 */
@Data
public class MachineStore {

    /**
     * 发布机器id
     */
    private Long id;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 发布机器
     */
    private ApplicationReleaseMachineDO machine;

    /**
     * 日志流
     */
    private OutputStream logStream;

    /**
     * 日志路径
     */
    private String logPath;

    /**
     * 机器会话
     */
    private SessionStore sessionStore;

    /**
     * 处理器
     */
    private List<IReleaseHandler> handler;

    /**
     * actions
     */
    private Map<Long, ApplicationReleaseActionDO> actions;

    public MachineStore() {
        this.actions = Maps.newLinkedHashMap();
    }

}
