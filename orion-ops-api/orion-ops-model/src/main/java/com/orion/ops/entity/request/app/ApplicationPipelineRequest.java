package com.orion.ops.entity.request.app;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 应用流水线请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用流水线请求")
@SuppressWarnings("ALL")
public class ApplicationPipelineRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "idList")
    private List<Long> idList;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    @ApiModelProperty(value = "环境id")
    private List<Long> profileIdList;

    @ApiModelProperty(value = "流水线名称")
    private String name;

    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     */
    @ApiModelProperty(value = "是否查询详情")
    private Integer queryDetail;

    @ApiModelProperty(value = "详情")
    private List<ApplicationPipelineDetailRequest> details;

}
