package com.orion.ops.handler.build;

import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.entity.domain.ApplicationBuildActionDO;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.remote.channel.SessionStore;
import com.orion.utils.collect.Maps;
import com.orion.utils.io.Files1;
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
     * vcs clone 路径
     */
    private String vcsClonePath;

    public BuildStore() {
        this.actions = Maps.newLinkedMap();
    }

    public void setBuildRecord(ApplicationBuildDO buildRecord) {
        this.buildRecord = buildRecord;
        this.vcsClonePath = Files1.getPath(MachineEnvAttr.VCS_PATH.getValue(), buildRecord.getVcsId() + "/" + buildRecord.getBuildSeq());
    }

}
