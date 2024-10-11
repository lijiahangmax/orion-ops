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
package com.orion.ops.mapping.app;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.constant.app.BuildStatus;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.ops.entity.dto.ApplicationBuildStatisticsDTO;
import com.orion.ops.entity.vo.app.*;
import com.orion.ops.utils.Utils;

import java.util.Date;
import java.util.Optional;

/**
 * 应用构建 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 17:36
 */
public class ApplicationBuildConversion {

    static {
        TypeStore.STORE.register(ApplicationBuildDO.class, ApplicationBuildReleaseListVO.class, p -> {
            ApplicationBuildReleaseListVO vo = new ApplicationBuildReleaseListVO();
            vo.setId(p.getId());
            vo.setSeq(p.getBuildSeq());
            vo.setDescription(p.getDescription());
            vo.setCreateTime(p.getCreateTime());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationBuildDO.class, ApplicationBuildStatisticsRecordVO.class, p -> {
            ApplicationBuildStatisticsRecordVO vo = new ApplicationBuildStatisticsRecordVO();
            vo.setBuildId(p.getId());
            vo.setSeq(p.getBuildSeq());
            vo.setBuildDate(p.getBuildStartTime());
            vo.setStatus(p.getBuildStatus());
            // 设置构建用时
            if (BuildStatus.FINISH.getStatus().equals(p.getBuildStatus())
                    && p.getBuildStartTime() != null
                    && p.getBuildEndTime() != null) {
                long used = p.getBuildEndTime().getTime() - p.getBuildStartTime().getTime();
                vo.setUsed(used);
                vo.setUsedInterval(Utils.interval(used));
            }
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationBuildDO.class, ApplicationBuildStatusVO.class, p -> {
            ApplicationBuildStatusVO vo = new ApplicationBuildStatusVO();
            vo.setId(p.getId());
            vo.setStatus(p.getBuildStatus());
            Date startTime = p.getBuildStartTime(), endTime = p.getBuildEndTime();
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
        TypeStore.STORE.register(ApplicationBuildDO.class, ApplicationBuildVO.class, p -> {
            ApplicationBuildVO vo = new ApplicationBuildVO();
            vo.setId(p.getId());
            vo.setAppId(p.getAppId());
            vo.setAppName(p.getAppName());
            vo.setAppTag(p.getAppTag());
            vo.setProfileId(p.getProfileId());
            vo.setProfileName(p.getProfileName());
            vo.setProfileTag(p.getProfileTag());
            vo.setSeq(p.getBuildSeq());
            vo.setRepoId(p.getRepoId());
            vo.setBranchName(p.getBranchName());
            vo.setCommitId(p.getCommitId());
            vo.setStatus(p.getBuildStatus());
            vo.setDescription(p.getDescription());
            vo.setCreateUserId(p.getCreateUserId());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            Date startTime = p.getBuildStartTime();
            Date endTime = p.getBuildEndTime();
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
        TypeStore.STORE.register(ApplicationBuildStatisticsDTO.class, ApplicationBuildStatisticsChartVO.class, p -> {
            ApplicationBuildStatisticsChartVO vo = new ApplicationBuildStatisticsChartVO();
            vo.setDate(Dates.format(p.getDate(), Dates.YMD));
            vo.setBuildCount(p.getBuildCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationBuildStatisticsDTO.class, ApplicationBuildStatisticsMetricsVO.class, p -> {
            ApplicationBuildStatisticsMetricsVO vo = new ApplicationBuildStatisticsMetricsVO();
            vo.setBuildCount(p.getBuildCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            vo.setAvgUsed(p.getAvgUsed());
            vo.setAvgUsedInterval(Utils.interval(p.getAvgUsed()));
            return vo;
        });
    }

}
