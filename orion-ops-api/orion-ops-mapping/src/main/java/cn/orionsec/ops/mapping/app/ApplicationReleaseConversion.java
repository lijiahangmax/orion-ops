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

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.ops.constant.app.ReleaseStatus;
import cn.orionsec.ops.entity.domain.ApplicationReleaseDO;
import cn.orionsec.ops.entity.domain.ApplicationReleaseMachineDO;
import cn.orionsec.ops.entity.dto.ApplicationReleaseStatisticsDTO;
import cn.orionsec.ops.entity.vo.app.*;
import cn.orionsec.ops.utils.Utils;

import java.util.Date;
import java.util.Optional;

/**
 * 应用发布 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 17:53
 */
public class ApplicationReleaseConversion {

    static {
        TypeStore.STORE.register(ApplicationReleaseDO.class, ApplicationReleaseDetailVO.class, p -> {
            ApplicationReleaseDetailVO vo = new ApplicationReleaseDetailVO();
            vo.setId(p.getId());
            vo.setTitle(p.getReleaseTitle());
            vo.setDescription(p.getReleaseDescription());
            vo.setBuildId(p.getBuildId());
            vo.setBuildSeq(p.getBuildSeq());
            vo.setAppId(p.getAppId());
            vo.setAppName(p.getAppName());
            vo.setAppTag(p.getAppTag());
            vo.setProfileId(p.getProfileId());
            vo.setProfileName(p.getProfileName());
            vo.setProfileTag(p.getProfileTag());
            vo.setType(p.getReleaseType());
            vo.setStatus(p.getReleaseStatus());
            vo.setSerializer(p.getReleaseSerialize());
            vo.setExceptionHandler(p.getExceptionHandler());
            vo.setTimedRelease(p.getTimedRelease());
            vo.setTimedReleaseTime(p.getTimedReleaseTime());
            vo.setCreateUserId(p.getCreateUserId());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setAuditUserId(p.getAuditUserId());
            vo.setAuditUserName(p.getAuditUserName());
            vo.setAuditTime(p.getAuditTime());
            vo.setAuditReason(p.getAuditReason());
            vo.setStartTime(p.getReleaseStartTime());
            vo.setEndTime(p.getReleaseEndTime());
            vo.setReleaseUserName(p.getReleaseUserName());
            vo.setCreateTime(p.getCreateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            Date startTime = p.getReleaseStartTime();
            Date endTime = p.getReleaseEndTime();
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            Optional.ofNullable(p.getTimedReleaseTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setTimedReleaseTimeAgo);
            Optional.ofNullable(p.getAuditTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setAuditTimeAgo);
            Optional.ofNullable(startTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setStartTimeAgo);
            Optional.ofNullable(endTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setEndTimeAgo);
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationReleaseDO.class, ApplicationReleaseListVO.class, p -> {
            ApplicationReleaseListVO vo = new ApplicationReleaseListVO();
            vo.setId(p.getId());
            vo.setTitle(p.getReleaseTitle());
            vo.setDescription(p.getReleaseDescription());
            vo.setBuildId(p.getBuildId());
            vo.setBuildSeq(p.getBuildSeq());
            vo.setAppId(p.getAppId());
            vo.setAppName(p.getAppName());
            vo.setAppTag(p.getAppTag());
            vo.setType(p.getReleaseType());
            vo.setStatus(p.getReleaseStatus());
            vo.setSerializer(p.getReleaseSerialize());
            vo.setExceptionHandler(p.getExceptionHandler());
            vo.setTimedRelease(p.getTimedRelease());
            vo.setTimedReleaseTime(p.getTimedReleaseTime());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setCreateTime(p.getCreateTime());
            vo.setAuditUserName(p.getAuditUserName());
            vo.setAuditReason(p.getAuditReason());
            vo.setAuditTime(p.getAuditTime());
            Date startTime = p.getReleaseStartTime();
            vo.setReleaseUserName(p.getReleaseUserName());
            vo.setReleaseTime(startTime);
            Date endTime = p.getReleaseEndTime();
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationReleaseMachineDO.class, ApplicationReleaseMachineVO.class, p -> {
            ApplicationReleaseMachineVO vo = new ApplicationReleaseMachineVO();
            vo.setId(p.getId());
            vo.setReleaseId(p.getReleaseId());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineTag(p.getMachineTag());
            vo.setMachineHost(p.getMachineHost());
            vo.setStatus(p.getRunStatus());
            Date startTime = p.getStartTime();
            Date endTime = p.getEndTime();
            vo.setStartTime(startTime);
            vo.setEndTime(endTime);
            vo.setStartTimeAgo(Optional.ofNullable(startTime).map(Dates::ago).orElse(null));
            vo.setEndTimeAgo(Optional.ofNullable(endTime).map(Dates::ago).orElse(null));
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationReleaseMachineDO.class, ApplicationReleaseMachineStatusVO.class, p -> {
            ApplicationReleaseMachineStatusVO vo = new ApplicationReleaseMachineStatusVO();
            vo.setId(p.getId());
            vo.setStatus(p.getRunStatus());
            Date startTime = p.getStartTime(), endTime = p.getEndTime();
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
        TypeStore.STORE.register(ApplicationReleaseStatisticsDTO.class, ApplicationReleaseStatisticsChartVO.class, p -> {
            ApplicationReleaseStatisticsChartVO vo = new ApplicationReleaseStatisticsChartVO();
            vo.setDate(Dates.format(p.getDate(), Dates.YMD));
            vo.setReleaseCount(p.getReleaseCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationReleaseMachineDO.class, ApplicationReleaseStatisticsMachineVO.class, p -> {
            ApplicationReleaseStatisticsMachineVO vo = new ApplicationReleaseStatisticsMachineVO();
            vo.setId(p.getId());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setStatus(p.getRunStatus());
            // 设置构建用时
            if (p.getStartTime() != null && p.getEndTime() != null) {
                long used = p.getEndTime().getTime() - p.getStartTime().getTime();
                vo.setUsed(used);
                vo.setUsedInterval(Utils.interval(used));
            }
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationReleaseStatisticsDTO.class, ApplicationReleaseStatisticsMetricsVO.class, p -> {
            ApplicationReleaseStatisticsMetricsVO vo = new ApplicationReleaseStatisticsMetricsVO();
            vo.setReleaseCount(p.getReleaseCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            vo.setAvgUsed(p.getAvgUsed());
            vo.setAvgUsedInterval(Utils.interval(p.getAvgUsed()));
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationReleaseDO.class, ApplicationReleaseStatisticsRecordVO.class, p -> {
            ApplicationReleaseStatisticsRecordVO vo = new ApplicationReleaseStatisticsRecordVO();
            vo.setReleaseId(p.getId());
            vo.setReleaseTitle(p.getReleaseTitle());
            vo.setReleaseDate(p.getReleaseStartTime());
            vo.setStatus(p.getReleaseStatus());
            // 设置构建用时
            if (ReleaseStatus.FINISH.getStatus().equals(p.getReleaseStatus())
                    && p.getReleaseStartTime() != null
                    && p.getReleaseEndTime() != null) {
                long used = p.getReleaseEndTime().getTime() - p.getReleaseStartTime().getTime();
                vo.setUsed(used);
                vo.setUsedInterval(Utils.interval(used));
            }
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationReleaseDO.class, ApplicationReleaseStatusVO.class, p -> {
            ApplicationReleaseStatusVO vo = new ApplicationReleaseStatusVO();
            vo.setId(p.getId());
            vo.setStatus(p.getReleaseStatus());
            Date startTime = p.getReleaseStartTime(), endTime = p.getReleaseEndTime();
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

}
