package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ReleaseBillDO;
import com.orion.utils.Objects1;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 上线单详情
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 19:02
 */
@Data
public class ReleaseBillDetailVO {

    /**
     * id
     */
    private Long id;

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
     * 发布状态 10待审核 20待发布 30发布中 40发布完成 50发布异常
     *
     * @see com.orion.ops.consts.app.ReleaseStatus
     */
    private Integer status;

    /**
     * 版本控制远程url
     */
    private String vcsRemoteUrl;

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
     * 创建人id
     */
    private Long createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 审核人id
     */
    private Long auditUserId;

    /**
     * 审核人名称
     */
    private String auditUserName;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核时间
     */
    private String auditTimeAgo;

    /**
     * 审核备注
     */
    private String auditReason;

    /**
     * 发布人id
     */
    private Long releaseUserId;

    /**
     * 发布人名称
     */
    private String releaseUserName;

    /**
     * 发布开始时间
     */
    private Date releaseStartTime;

    /**
     * 发布结束时间
     */
    private Date releaseEndTime;

    /**
     * 发布开始时间
     */
    private String releaseStartTimeAgo;

    /**
     * 发布结束时间
     */
    private String releaseEndTimeAgo;

    /**
     * 发布使用时间
     */
    private String releaseUsedTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建时间
     */
    private String createTimeAgo;

    /**
     * 机器列表
     */
    private List<ReleaseMachineVO> machines;

    /**
     * 宿主机操作
     */
    private List<ReleaseActionVO> hostActions;

    static {
        TypeStore.STORE.register(ReleaseBillDO.class, ReleaseBillDetailVO.class, p -> {
            ReleaseBillDetailVO vo = new ReleaseBillDetailVO();
            vo.setId(p.getId());
            vo.setTitle(p.getReleaseTitle());
            vo.setDescription(p.getReleaseDescription());
            vo.setAppId(p.getAppId());
            vo.setAppName(p.getAppName());
            vo.setProfileId(p.getProfileId());
            vo.setProfileName(p.getProfileName());
            vo.setType(p.getReleaseType());
            vo.setStatus(p.getReleaseStatus());
            vo.setVcsRemoteUrl(p.getVcsRemoteUrl());
            vo.setBranchName(p.getBranchName());
            vo.setCommitId(p.getCommitId());
            vo.setDistPath(p.getDistPath());
            vo.setCreateUserId(p.getCreateUserId());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setAuditUserId(p.getAuditUserId());
            vo.setAuditUserName(p.getAuditUserName());
            vo.setAuditTime(p.getAuditTime());
            Optional.ofNullable(p.getAuditTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setAuditTimeAgo);
            vo.setAuditReason(p.getAuditReason());
            vo.setReleaseUserId(p.getReleaseUserId());
            vo.setReleaseUserName(p.getReleaseUserName());
            Date releaseStartTime = p.getReleaseStartTime();
            Date releaseEndTime = p.getReleaseEndTime();
            vo.setReleaseStartTime(releaseStartTime);
            vo.setReleaseEndTime(releaseEndTime);
            Optional.ofNullable(releaseStartTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setReleaseStartTimeAgo);
            Optional.ofNullable(releaseEndTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setReleaseEndTimeAgo);
            if (releaseStartTime != null) {
                String interval = Dates.interval(releaseStartTime, Objects1.def(releaseEndTime, new Date()), "d", "h", "m", "s");
                vo.setReleaseUsedTime(interval);
            }
            vo.setCreateTime(p.getCreateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            return vo;
        });
    }

}
