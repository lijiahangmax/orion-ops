package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationBuildActionDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Date;

/**
 * 应用构建操作状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/8 15:56
 */
@Data
public class ApplicationBuildActionStatusVO {

    /**
     * id
     */
    private Long id;

    /**
     * @see com.orion.ops.consts.app.ActionStatus
     */
    private Integer status;

    /**
     * used
     */
    private Long used;

    static {
        TypeStore.STORE.register(ApplicationBuildActionDO.class, ApplicationBuildActionStatusVO.class, p -> {
            ApplicationBuildActionStatusVO vo = new ApplicationBuildActionStatusVO();
            vo.setId(p.getId());
            vo.setStatus(p.getRunStatus());
            Date startTime = p.getStartTime(), endTime = p.getEndTime();
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
            }
            return vo;
        });
    }

}
