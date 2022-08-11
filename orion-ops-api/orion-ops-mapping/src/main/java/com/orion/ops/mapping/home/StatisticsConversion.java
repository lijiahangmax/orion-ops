package com.orion.ops.mapping.home;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.dto.statistic.StatisticsCountDTO;
import com.orion.ops.entity.vo.home.HomeStatisticsCountVO;

/**
 * 统计数据 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:09
 */
public class StatisticsConversion {

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
