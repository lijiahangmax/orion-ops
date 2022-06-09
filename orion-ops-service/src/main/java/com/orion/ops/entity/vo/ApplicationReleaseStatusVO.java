package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationReleaseDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 应用发布状态响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/20 9:53
 */
@Data
@ApiModel(value = "应用发布状态响应")
public class ApplicationReleaseStatusVO {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * @see com.orion.ops.consts.app.ReleaseStatus
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "发布开始时间")
    private Date startTime;

    @ApiModelProperty(value = "发布开始时间")
    private String startTimeAgo;

    @ApiModelProperty(value = "发布结束时间")
    private Date endTime;

    @ApiModelProperty(value = "发布结束时间")
    private String endTimeAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "机器状态")
    private List<ApplicationReleaseMachineStatusVO> machines;

    static {
        TypeStore.STORE.register(ApplicationReleaseDO.class, ApplicationReleaseStatusVO.class, p -> {
            ApplicationReleaseStatusVO vo = new ApplicationReleaseStatusVO();
            vo.setId(p.getId());
            vo.setStatus(p.getReleaseStatus());
            Date startTime = p.getReleaseStartTime(), endTime = p.getReleaseEndTime();
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
