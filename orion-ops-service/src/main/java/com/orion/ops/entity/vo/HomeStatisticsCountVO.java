package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.dto.StatisticsCountDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 首页统计数量返回响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/20 14:08
 */
@Data
@ApiModel(value = "首页统计数量返回响应")
public class HomeStatisticsCountVO {

    @ApiModelProperty(value = "机器数量")
    private Integer machineCount;

    @ApiModelProperty(value = "环境数量")
    private Integer profileCount;

    @ApiModelProperty(value = "应用数量")
    private Integer appCount;

    @ApiModelProperty(value = "流水线数量")
    private Integer pipelineCount;

    static {
        TypeStore.STORE.register(StatisticsCountDTO.class, HomeStatisticsCountVO.class, p -> {
            HomeStatisticsCountVO vo = new HomeStatisticsCountVO();
            vo.setMachineCount(p.getMachineCount());
            vo.setProfileCount(p.getProfileCount());
            vo.setAppCount(p.getAppCount());
            vo.setPipelineCount(p.getPipelineCount());
            return vo;
        });
    }

}
