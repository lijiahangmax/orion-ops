package com.orion.ops.entity.vo.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 机器报警配置包装响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/29 15:31
 */
@Data
@ApiModel(value = "机器报警配置包装响应")
public class MachineAlarmConfigWrapperVO {

    @ApiModelProperty(value = "配置")
    private List<MachineAlarmConfigVO> config;

    @ApiModelProperty(value = "报警组id")
    private List<Long> groupIdList;

}
