package com.orion.ops.handler.build;

import com.orion.able.Executable;
import com.orion.ops.entity.domain.ApplicationBuildActionDO;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.ops.entity.dto.ApplicationBuildStatusDTO;
import com.orion.remote.channel.SessionStore;
import com.orion.utils.collect.Maps;
import lombok.Data;

import java.io.OutputStream;
import java.util.Map;

/**
 * 构建存储对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/5 22:26
 */
@Data
public class BuildStore {

    /**
     * 状态
     */
    private ApplicationBuildStatusDTO status;

    /**
     * session
     */
    private SessionStore sessionStore;

    /**
     * record
     */
    private ApplicationBuildDO buildRecord;

    /**
     * key: action.id
     * value: action
     */
    private Map<Long, ApplicationBuildActionDO> actions;

    /**
     * 日志流
     */
    private OutputStream mainLogStream;

    /**
     * 日志文件
     */
    private String mainLogPath;

    /**
     * 同步状态
     */
    private Executable syncStatus;

    public BuildStore() {
        this.status = new ApplicationBuildStatusDTO();
        this.actions = Maps.newLinkedMap();
    }

}
