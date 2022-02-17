package com.orion.ops.handler.app.action;

import com.beust.jcommander.internal.Maps;
import com.orion.ops.entity.domain.ApplicationActionLogDO;
import com.orion.remote.channel.SessionStore;
import lombok.Data;

import java.io.OutputStream;
import java.util.Map;

/**
 * 发布操作参数
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/11 16:05
 */
@Data
public class MachineActionStore {

    /**
     * 引用id
     */
    private Long relId;

    /**
     * action
     */
    private Map<Long, ApplicationActionLogDO> actions;

    /**
     * 日志输出流
     */
    private OutputStream superLogStream;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器会话
     */
    private SessionStore sessionStore;

    /**
     * 版本id
     *
     * @see com.orion.ops.handler.app.action.CheckoutActionHandler
     */
    private Long vcsId;

    /**
     * 分支
     *
     * @see com.orion.ops.handler.app.action.CheckoutActionHandler
     */
    private String branchName;

    /**
     * 提交版本
     *
     * @see com.orion.ops.handler.app.action.CheckoutActionHandler
     */
    private String commitId;

    /**
     * vcs clone 路径
     *
     * @see com.orion.ops.handler.app.action.CheckoutActionHandler
     */
    private String vcsClonePath;

    /**
     * 构建产物文件
     *
     * @see com.orion.ops.handler.app.action.TransferActionHandler
     */
    private String bundlePath;

    /**
     * 产物传输路径
     *
     * @see com.orion.ops.handler.app.action.TransferActionHandler
     */
    private String transferPath;

    public MachineActionStore() {
        this.actions = Maps.newLinkedHashMap();
    }

}
