package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 流水线日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/24 20:49
 */
@Data
@ApiModel(value = "流水线日志响应")
@SuppressWarnings("ALL")
public class ApplicationPipelineTaskLogVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "流水线任务id")
    private Long taskId;

    @ApiModelProperty(value = "流水线任务详情id")
    private Long taskDetailId;

    /**
     * @see com.orion.ops.constant.app.PipelineLogStatus
     */
    @ApiModelProperty(value = "日志状态 10创建 20执行 30成功 40失败 50停止 60跳过")
    private Integer status;

    /**
     * @see com.orion.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    private Integer type;

    @ApiModelProperty(value = "日志详情")
    private String log;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建时间")
    private String createTimeAgo;

}
