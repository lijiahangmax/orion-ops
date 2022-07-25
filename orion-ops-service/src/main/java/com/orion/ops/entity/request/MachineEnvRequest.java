package com.orion.ops.entity.request;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 机器环境变量请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 11:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "机器环境变量请求")
public class MachineEnvRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "key")
    private String key;

    @ApiModelProperty(value = "value")
    private String value;

    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * @see com.orion.ops.constant.env.EnvViewType
     */
    @ApiModelProperty(value = "视图类型")
    private Integer viewType;

    @ApiModelProperty(value = "目标机器id")
    private List<Long> targetMachineIdList;

}
