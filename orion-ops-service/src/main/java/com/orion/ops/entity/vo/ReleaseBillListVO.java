package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ReleaseBillDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;

/**
 * 上线单列表
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 18:58
 */
@Data
public class ReleaseBillListVO {

    /**
     * id
     */
    private Long id;

    /**
     * 发布标题
     */
    private String title;

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
     * 发布分支
     */
    private String branchName;

    /**
     * 发布提交id
     */
    private String commitId;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建时间
     */
    private String createTimeAgo;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改时间
     */
    private String updateTimeAgo;

    static {
        TypeStore.STORE.register(ReleaseBillDO.class, ReleaseBillListVO.class, p -> {
            ReleaseBillListVO vo = new ReleaseBillListVO();
            vo.setId(p.getId());
            vo.setTitle(p.getReleaseTitle());
            vo.setAppId(p.getAppId());
            vo.setAppName(p.getAppName());
            vo.setProfileId(p.getProfileId());
            vo.setProfileName(p.getProfileName());
            vo.setType(p.getReleaseType());
            vo.setStatus(p.getReleaseStatus());
            vo.setBranchName(p.getBranchName());
            vo.setCommitId(p.getCommitId());
            vo.setCreateUserId(p.getCreateUserId());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setCreateTime(p.getCreateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            vo.setUpdateTime(p.getUpdateTime());
            vo.setUpdateTimeAgo(Dates.ago(p.getUpdateTime()));
            return vo;
        });
    }

}
