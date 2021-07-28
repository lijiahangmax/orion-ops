package com.orion.ops.handler.release.hint;

import com.orion.remote.channel.SessionStore;
import lombok.Data;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 上线单配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/17 0:29
 */
@Data
public class ReleaseHint {

    /**
     * 上线单id
     */
    private Long releaseId;

    /**
     * 发布标题
     */
    private String title;

    /**
     * 发布描述
     */
    private String description;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * 环境名称
     */
    private String profileName;

    /**
     * 发布类型 10正常发布 20回滚发布
     *
     * @see com.orion.ops.consts.app.ReleaseType
     */
    private Integer type;

    /**
     * 版本控制本地目录
     */
    private String vcsLocalPath;

    /**
     * 发布分支
     */
    private String branchName;

    /**
     * 发布提交id
     */
    private String commitId;

    /**
     * 产物目录
     */
    private String distPath;

    /**
     * 产物快照目录
     */
    private String distSnapshotPath;

    /**
     * 发布人id
     */
    private Long releaseUserId;

    /**
     * 发布人名称
     */
    private String releaseUserName;

    /**
     * 宿主机日志
     */
    private String hostLogPath;

    /**
     * 宿主机日志流
     */
    private OutputStream hostLogOutputStream;

    /**
     * 机器列表
     */
    private List<ReleaseMachineHint> machines;

    /**
     * 机器连接会话
     */
    private Map<Long, SessionStore> sessionHolder;

}
