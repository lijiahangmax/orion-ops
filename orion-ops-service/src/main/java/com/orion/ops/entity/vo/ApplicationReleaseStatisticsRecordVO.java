package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.consts.app.ReleaseStatus;
import com.orion.ops.entity.domain.ApplicationReleaseDO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 应用发布明细响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/31 8:46
 */
@ApiModel(value = "应用发布明细响应")
@Data
public class ApplicationReleaseStatisticsRecordVO {

    @ApiModelProperty(value = "发布id")
    private Long releaseId;

    @ApiModelProperty(value = "发布标题")
    private String releaseTitle;

    @ApiModelProperty(value = "发布时间")
    private Date releaseDate;

    /**
     * @see com.orion.ops.consts.app.ReleaseStatus
     */
    @ApiModelProperty(value = "发布状态 10待审核 20审核驳回 30待发布 35待调度 40发布中 50发布完成 60发布停止 70发布失败")
    private Integer status;

    @ApiModelProperty(value = "成功构建操作时长毫秒")
    private Long used;

    @ApiModelProperty(value = "成功构建操作时长")
    private String usedInterval;

    @ApiModelProperty(value = "发布机器")
    private List<ApplicationReleaseStatisticsMachineVO> machines;

    static {
        TypeStore.STORE.register(ApplicationReleaseDO.class, ApplicationReleaseStatisticsRecordVO.class, p -> {
            ApplicationReleaseStatisticsRecordVO vo = new ApplicationReleaseStatisticsRecordVO();
            vo.setReleaseId(p.getId());
            vo.setReleaseTitle(p.getReleaseTitle());
            vo.setReleaseDate(p.getReleaseStartTime());
            vo.setStatus(p.getReleaseStatus());
            // 设置构建用时
            if (ReleaseStatus.FINISH.getStatus().equals(p.getReleaseStatus())
                    && p.getReleaseStartTime() != null
                    && p.getReleaseEndTime() != null) {
                long used = p.getReleaseEndTime().getTime() - p.getReleaseStartTime().getTime();
                vo.setUsed(used);
                vo.setUsedInterval(Utils.interval(used));
            }
            return vo;
        });
    }

}
