package com.orion.ops.entity.vo;

import com.orion.ops.entity.dto.StatisticsCountDTO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 首页统计数量返回
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/20 14:08
 */
@Data
public class HomeStatisticsCountVO {

    /**
     * 机器数量
     */
    private Integer machineCount;

    /**
     * 环境数量
     */
    private Integer profileCount;

    /**
     * 应用数量
     */
    private Integer appCount;

    /**
     * 流水线数量
     */
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
