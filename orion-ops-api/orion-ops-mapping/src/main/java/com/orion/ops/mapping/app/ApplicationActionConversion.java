package com.orion.ops.mapping.app;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.ApplicationActionDO;
import com.orion.ops.entity.domain.ApplicationActionLogDO;
import com.orion.ops.entity.vo.app.*;
import com.orion.ops.utils.Utils;

import java.util.Date;
import java.util.Optional;

/**
 * 应用操作 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 17:34
 */
public class ApplicationActionConversion {

    static {
        TypeStore.STORE.register(ApplicationActionDO.class, ApplicationActionVO.class, p -> {
            ApplicationActionVO vo = new ApplicationActionVO();
            vo.setId(p.getId());
            vo.setName(p.getActionName());
            vo.setType(p.getActionType());
            vo.setCommand(p.getActionCommand());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationActionLogDO.class, ApplicationActionLogVO.class, p -> {
            ApplicationActionLogVO vo = new ApplicationActionLogVO();
            vo.setId(p.getId());
            vo.setRelId(p.getRelId());
            vo.setActionId(p.getActionId());
            vo.setActionName(p.getActionName());
            vo.setActionType(p.getActionType());
            vo.setActionCommand(p.getActionCommand());
            vo.setStatus(p.getRunStatus());
            vo.setExitCode(p.getExitCode());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            Date startTime = p.getStartTime();
            Date endTime = p.getEndTime();
            vo.setStartTime(startTime);
            vo.setStartTimeAgo(Optional.ofNullable(startTime).map(Dates::ago).orElse(null));
            vo.setEndTime(endTime);
            vo.setEndTimeAgo(Optional.ofNullable(endTime).map(Dates::ago).orElse(null));
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationActionLogDO.class, ApplicationActionStatusVO.class, p -> {
            ApplicationActionStatusVO vo = new ApplicationActionStatusVO();
            vo.setId(p.getId());
            vo.setStatus(p.getRunStatus());
            Date startTime = p.getStartTime(), endTime = p.getEndTime();
            vo.setStartTime(startTime);
            vo.setStartTimeAgo(Optional.ofNullable(startTime).map(Dates::ago).orElse(null));
            vo.setEndTime(endTime);
            vo.setEndTimeAgo(Optional.ofNullable(endTime).map(Dates::ago).orElse(null));
            vo.setExitCode(p.getExitCode());
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationActionLogDO.class, ApplicationActionLogStatisticsVO.class, p -> {
            ApplicationActionLogStatisticsVO vo = new ApplicationActionLogStatisticsVO();
            vo.setId(p.getId());
            vo.setStatus(p.getRunStatus());
            Date startTime = p.getStartTime();
            Date endTime = p.getEndTime();
            if (startTime != null && endTime != null) {
                long used = endTime.getTime() - startTime.getTime();
                vo.setUsed(used);
                vo.setUsedInterval(Utils.interval(used));
            }
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationActionDO.class, ApplicationActionStatisticsVO.class, p -> {
            ApplicationActionStatisticsVO vo = new ApplicationActionStatisticsVO();
            vo.setId(p.getId());
            vo.setName(p.getActionName());
            return vo;
        });
    }

}
