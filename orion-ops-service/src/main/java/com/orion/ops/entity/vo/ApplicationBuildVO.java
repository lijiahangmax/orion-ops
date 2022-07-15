package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 应用构建详情响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/3 14:09
 */
@Data
@ApiModel(value = "应用构建详情响应")
public class ApplicationBuildVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "应用唯一标识")
    private String appTag;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    @ApiModelProperty(value = "环境名称")
    private String profileName;

    @ApiModelProperty(value = "环境唯一标识")
    private String profileTag;

    @ApiModelProperty(value = "构建序列")
    private Integer seq;

    @ApiModelProperty(value = "版本仓库id")
    private Long vcsId;

    @ApiModelProperty(value = "版本仓库名称")
    private String vcsName;

    @ApiModelProperty(value = "构建分支")
    private String branchName;

    @ApiModelProperty(value = "构建提交id")
    private String commitId;

    /**
     * @see com.orion.ops.constant.app.BuildStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20执行中 30已完成 40执行失败 50已取消")
    private Integer status;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "创建人id")
    private Long createUserId;

    @ApiModelProperty(value = "创建人名称")
    private String createUserName;

    @ApiModelProperty(value = "构建开始时间")
    private Date startTime;

    @ApiModelProperty(value = "构建开始时间")
    private String startTimeAgo;

    @ApiModelProperty(value = "构建结束时间")
    private Date endTime;

    @ApiModelProperty(value = "构建结束时间")
    private String endTimeAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "构建操作")
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
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

}
