/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.mapping.app;

import cn.orionsec.ops.entity.domain.ApplicationActionDO;
import cn.orionsec.ops.entity.domain.ApplicationActionLogDO;
import cn.orionsec.ops.entity.vo.app.*;
import cn.orionsec.ops.utils.Utils;
import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;

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
