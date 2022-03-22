package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationReleaseDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 应用发布vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/20 9:43
 */
@Data
public class ApplicationReleaseDetailVO {

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
     * 构建id
     */
    private Long buildId;

    /**
     * 构建序列
     */
    private Integer buildSeq;

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
     * 发布类型 10正常发布 20回滚发布
     *
     * @see com.orion.ops.consts.app.ReleaseType
     */
    private Integer type;

    /**
     * 发布状态 10待审核 20审核驳回 30待发布 40发布中 50发布完成 60发布停止 70初始化失败 80发布失败
     *
     * @see com.orion.ops.consts.app.ReleaseStatus
     */
    private Integer status;

    /**
     * 发布序列 10串行 20并行
     *
     * @see com.orion.ops.consts.SerialType
     */
    private Integer serializer;

    /**
     * 异常处理 10跳过所有 20跳过错误
     *
     * @see com.orion.ops.consts.ExceptionHandlerType
     */
    private Integer exceptionHandler;

    /**
     * 是否是定时发布 10普通发布 20定时发布
     *
     * @see com.orion.ops.consts.app.TimedReleaseType
     */
    private Integer timedRelease;

    /**
     * 定时发布时间
     */
    private Date timedReleaseTime;

    /**
     * 定时发布时间
     */
    private String timedReleaseTimeAgo;

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
     * 发布开始时间
     */
    private Date startTime;

    /**
     * 发布开始时间
     */
    private String startTimeAgo;

    /**
     * 发布结束时间
     */
    private Date endTime;

    /**
     * 发布结束时间
     */
    private String endTimeAgo;

    /**
     * 发布人名称
     */
    private String releaseUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建时间
     */
    private String createTimeAgo;

    /**
     * 使用时间 ms
     */
    private Long used;

    /**
     * 使用时间
     */
    private String keepTime;

    /**
     * 操作
     */
    private List<ApplicationActionVO> actions;

    /**
     * 发布机器
     */
    private List<ApplicationReleaseMachineVO> machines;

    static {
        TypeStore.STORE.register(ApplicationReleaseDO.class, ApplicationReleaseDetailVO.class, p -> {
            ApplicationReleaseDetailVO vo = new ApplicationReleaseDetailVO();
            vo.setId(p.getId());
            vo.setTitle(p.getReleaseTitle());
            vo.setDescription(p.getReleaseDescription());
            vo.setBuildId(p.getBuildId());
            vo.setBuildSeq(p.getBuildSeq());
            vo.setAppId(p.getAppId());
            vo.setAppName(p.getAppName());
            vo.setAppTag(p.getAppTag());
            vo.setProfileId(p.getProfileId());
            vo.setProfileName(p.getProfileName());
            vo.setProfileTag(p.getProfileTag());
            vo.setType(p.getReleaseType());
            vo.setStatus(p.getReleaseStatus());
            vo.setSerializer(p.getReleaseSerialize());
            vo.setExceptionHandler(p.getExceptionHandler());
            vo.setTimedRelease(p.getTimedRelease());
            vo.setTimedReleaseTime(p.getTimedReleaseTime());
            vo.setCreateUserId(p.getCreateUserId());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setAuditUserId(p.getAuditUserId());
            vo.setAuditUserName(p.getAuditUserName());
            vo.setAuditTime(p.getAuditTime());
            vo.setAuditReason(p.getAuditReason());
            vo.setStartTime(p.getReleaseStartTime());
            vo.setEndTime(p.getReleaseEndTime());
            vo.setReleaseUserName(p.getReleaseUserName());
            vo.setCreateTime(p.getCreateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            Date startTime = p.getReleaseStartTime();
            Date endTime = p.getReleaseEndTime();
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            Optional.ofNullable(p.getTimedReleaseTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setTimedReleaseTimeAgo);
            Optional.ofNullable(p.getAuditTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setAuditTimeAgo);
            Optional.ofNullable(startTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setStartTimeAgo);
            Optional.ofNullable(endTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setEndTimeAgo);
            return vo;
        });
    }

}
