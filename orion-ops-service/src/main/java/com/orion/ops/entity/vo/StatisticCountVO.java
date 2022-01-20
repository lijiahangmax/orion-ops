package com.orion.ops.entity.vo;

import com.orion.ops.entity.dto.StatisticCountDTO;
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
public class StatisticCountVO {

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
     * 日志数量
     */
    private Integer logCount;

    static {
        TypeStore.STORE.register(StatisticCountDTO.class, StatisticCountVO.class, p -> {
            StatisticCountVO vo = new StatisticCountVO();
            vo.setMachineCount(p.getMachineCount());
            vo.setProfileCount(p.getProfileCount());
            vo.setAppCount(p.getAppCount());
            vo.setLogCount(p.getLogCount());
            return vo;
        });
    }

}
