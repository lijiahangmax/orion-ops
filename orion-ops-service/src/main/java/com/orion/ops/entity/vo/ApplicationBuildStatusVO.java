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
 * 应用构建状态响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/3 14:11
 */
@Data
@ApiModel(value = "应用构建状态响应")
public class ApplicationBuildStatusVO {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * @see com.orion.ops.constant.app.BuildStatus
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

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

    @ApiModelProperty(value = "构建操作")
    private List<ApplicationActionStatusVO> actions;

    static {
        TypeStore.STORE.register(ApplicationBuildDO.class, ApplicationBuildStatusVO.class, p -> {
            ApplicationBuildStatusVO vo = new ApplicationBuildStatusVO();
            vo.setId(p.getId());
            vo.setStatus(p.getBuildStatus());
            Date startTime = p.getBuildStartTime(), endTime = p.getBuildEndTime();
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
