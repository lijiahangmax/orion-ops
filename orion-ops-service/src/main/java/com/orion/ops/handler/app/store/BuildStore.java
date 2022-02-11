package com.orion.ops.handler.app.store;

import com.orion.ops.entity.domain.ApplicationBuildActionDO;
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
     * session
     */
    private SessionStore sessionStore;

    /**
     * 构建id
     */
    private Long buildId;

    /**
     * 版本id
     */
    private Long vcsId;

    /**
     * 分支
     */
    private String branchName;

    /**
     * 提交版本
     */
    private String commitId;

    /**
     * vcs clone 路径
     */
    private String vcsClonePath;

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

    public BuildStore() {
        this.actions = Maps.newLinkedMap();
    }

}
