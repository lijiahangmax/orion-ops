package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationReleaseDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 应用发布vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/20 9:43
 */
@Data
public class ApplicationReleaseListVO {

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
     * 发布类型 10正常发布 20回滚发布
     *
     * @see com.orion.ops.consts.app.ReleaseType
     */
    private Integer type;

    /**
     * 发布状态 10待审核 20审核驳回 30待发布 40发布中 50发布完成 60发布停止 70发布取消 80发布异常
     *
     * @see com.orion.ops.consts.app.ReleaseStatus
     */
    private Integer status;

    /**
     * 发布序列 10串行 20并行
     *
     * @see com.orion.ops.consts.app.ReleaseSerialType
     */
    private Integer serializer;

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
     * 创建人名称
     */
    private String createUserName;

    /**
     * 审核人名称
     */
    private String auditUserName;

    /**
     * 审核备注
     */
    private String auditReason;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 使用时间 ms
     */
    private Long used;

    /**
     * 使用时间
     */
    private String keepTime;

    /**
     * 发布机器
     */
    private List<ApplicationReleaseMachineVO> machines;

    static {
        TypeStore.STORE.register(ApplicationReleaseDO.class, ApplicationReleaseListVO.class, p -> {
            ApplicationReleaseListVO vo = new ApplicationReleaseListVO();
            vo.setId(p.getId());
            vo.setTitle(p.getReleaseTitle());
            vo.setDescription(p.getReleaseDescription());
            vo.setBuildId(p.getBuildId());
            vo.setAppId(p.getAppId());
            vo.setAppName(p.getAppName());
            vo.setAppTag(p.getAppTag());
            vo.setType(p.getReleaseType());
            vo.setStatus(p.getReleaseStatus());
            vo.setSerializer(p.getReleaseSerialize());
            vo.setTimedRelease(p.getTimedRelease());
            vo.setTimedReleaseTime(p.getTimedReleaseTime());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setAuditUserName(p.getAuditUserName());
            vo.setAuditReason(p.getAuditReason());
            vo.setCreateTime(p.getCreateTime());
            Date startTime = p.getReleaseStartTime();
            Date endTime = p.getReleaseEndTime();
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Dates.interval(vo.getUsed(), false, "d", "h", "m", "s"));
            }
            return vo;
        });
    }

}
