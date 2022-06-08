package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 应用发布请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/20 9:40
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用发布请求")
public class ApplicationReleaseRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    @ApiModelProperty(value = "构建id")
    private Long buildId;

    @ApiModelProperty(value = "应用机器id列表")
    private List<Long> machineIdList;

    /**
     * @see com.orion.ops.consts.app.ReleaseStatus
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * @see com.orion.ops.consts.app.TimedType
     */
    @ApiModelProperty(value = "是否是定时发布 10普通发布 20定时发布")
    private Integer timedRelease;

    @ApiModelProperty(value = "定时发布时间")
    private Date timedReleaseTime;

    /**
     * @see com.orion.ops.consts.Const#ENABLE
     */
    @ApiModelProperty(value = "只看自己")
    private Integer onlyMyself;

    @ApiModelProperty(value = "发布机器id")
    private Long releaseMachineId;

    @ApiModelProperty(value = "发布机器id")
    private List<Long> releaseMachineIdList;

    @ApiModelProperty(value = "id列表")
    private List<Long> idList;

    @ApiModelProperty(value = "是否查询机器")
    private Integer queryMachine;

    @ApiModelProperty(value = "是否查询操作")
    private Integer queryAction;

    @ApiModelProperty(value = "命令")
    private String command;

}
