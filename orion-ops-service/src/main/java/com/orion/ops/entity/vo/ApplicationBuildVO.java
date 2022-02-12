package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 应用构建详情
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/3 14:09
 */
@Data
public class ApplicationBuildVO {

    /**
     * id
     */
    private Long id;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用tag
     */
    private String appTag;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * 环境名称
     */
    private String profileName;

    /**
     * 环境tag
     */
    private String profileTag;

    /**
     * 构建序列
     */
    private Integer seq;

    /**
     * 版本仓库id
     */
    private Long vcsId;

    /**
     * 版本仓库名称
     */
    private String vcsName;

    /**
     * 构建分支
     */
    private String branchName;

    /**
     * 构建提交id
     */
    private String commitId;

    /**
     * 状态 10未开始 20执行中 30已完成 40执行失败 50已取消
     *
     * @see com.orion.ops.consts.app.BuildStatus
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 构建开始时间
     */
    private Date startTime;

    /**
     * 构建开始时间
     */
    private String startTimeAgo;

    /**
     * 构建结束时间
     */
    private Date endTime;

    /**
     * 构建结束时间
     */
    private String endTimeAgo;

    /**
     * 使用时间 ms
     */
    private Long used;

    /**
     * 使用时间
     */
    private String keepTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 操作
     */
    private List<ApplicationActionLogVO> actions;

    static {
        TypeStore.STORE.register(ApplicationBuildDO.class, ApplicationBuildVO.class, p -> {
            ApplicationBuildVO vo = new ApplicationBuildVO();
            vo.setId(p.getId());
            vo.setAppId(p.getAppId());
            vo.setAppName(p.getAppName());
            vo.setAppTag(p.getAppTag());
            vo.setProfileId(p.getProfileId());
            vo.setProfileName(p.getProfileName());
            vo.setProfileTag(p.getProfileTag());
            vo.setSeq(p.getBuildSeq());
            vo.setVcsId(p.getVcsId());
            vo.setBranchName(p.getBranchName());
            vo.setCommitId(p.getCommitId());
            vo.setStatus(p.getBuildStatus());
            vo.setDescription(p.getDescription());
            vo.setCreateUserId(p.getCreateUserId());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            Date startTime = p.getBuildStartTime();
            Date endTime = p.getBuildEndTime();
            vo.setStartTime(startTime);
            vo.setStartTimeAgo(Optional.ofNullable(startTime).map(Dates::ago).orElse(null));
            vo.setEndTime(endTime);
            vo.setEndTimeAgo(Optional.ofNullable(endTime).map(Dates::ago).orElse(null));
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Dates.interval(vo.getUsed(), false, "d", "h", "m", "s"));
            }
            return vo;
        });
    }

}
