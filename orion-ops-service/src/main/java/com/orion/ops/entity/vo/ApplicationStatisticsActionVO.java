package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationActionDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 应用构建统计分析操作
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 18:06
 */
@Data
public class ApplicationStatisticsActionVO {

    /**
     * id
     */
    private Long id;

    /**
     * 操作名称
     */
    private String name;

    /**
     * 平均操作时长ms (成功)
     */
    private Long avgUsed;

    /**
     * 平均操作时长 (成功)
     */
    private String avgUsedInterval;

    static {
        TypeStore.STORE.register(ApplicationActionDO.class, ApplicationStatisticsActionVO.class, p -> {
            ApplicationStatisticsActionVO vo = new ApplicationStatisticsActionVO();
            vo.setId(p.getId());
            vo.setName(p.getActionName());
            return vo;
        });
    }

}
