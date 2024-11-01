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

import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.ops.constant.app.PipelineStatus;
import cn.orionsec.ops.entity.domain.ApplicationPipelineTaskDO;
import cn.orionsec.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import cn.orionsec.ops.entity.domain.ApplicationPipelineTaskLogDO;
import cn.orionsec.ops.entity.dto.ApplicationPipelineTaskStatisticsDTO;
import cn.orionsec.ops.entity.dto.app.ApplicationPipelineStageConfigDTO;
import cn.orionsec.ops.entity.request.app.ApplicationPipelineTaskDetailRequest;
import cn.orionsec.ops.entity.vo.app.*;
import cn.orionsec.ops.utils.Utils;
import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.Optional;

/**
 * 应用流水线任务 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/8 10:00
 */
public class ApplicationPipelineTaskConversion {

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDetailRequest.class, ApplicationPipelineStageConfigDTO.class, p -> {
            ApplicationPipelineStageConfigDTO dto = new ApplicationPipelineStageConfigDTO();
            dto.setBranchName(p.getBranchName());
            dto.setCommitId(p.getCommitId());
            dto.setBuildId(p.getBuildId());
            dto.setTitle(p.getTitle());
            dto.setDescription(p.getDescription());
            dto.setMachineIdList(p.getMachineIdList());
            return dto;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDO.class, ApplicationPipelineTaskStatisticsTaskVO.class, p -> {
            ApplicationPipelineTaskStatisticsTaskVO vo = new ApplicationPipelineTaskStatisticsTaskVO();
            vo.setId(p.getId());
            vo.setTitle(p.getExecTitle());
            vo.setExecDate(p.getExecStartTime());
            vo.setStatus(p.getExecStatus());
            // 设置构建用时
            if (PipelineStatus.FINISH.getStatus().equals(p.getExecStatus())
                    && p.getExecStartTime() != null
                    && p.getExecEndTime() != null) {
                long used = p.getExecEndTime().getTime() - p.getExecStartTime().getTime();
                vo.setUsed(used);
                vo.setUsedInterval(Utils.interval(used));
            }
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDO.class, ApplicationPipelineTaskVO.class, p -> {
            ApplicationPipelineTaskVO vo = new ApplicationPipelineTaskVO();
            vo.setId(p.getId());
            vo.setPipelineId(p.getPipelineId());
            vo.setPipelineName(p.getPipelineName());
            vo.setProfileId(p.getProfileId());
            vo.setProfileName(p.getProfileName());
            vo.setProfileTag(p.getProfileTag());
            vo.setTitle(p.getExecTitle());
            vo.setDescription(p.getExecDescription());
            vo.setStatus(p.getExecStatus());
            vo.setTimedExec(p.getTimedExec());
            vo.setTimedExecTime(p.getTimedExecTime());
            vo.setCreateUserId(p.getCreateUserId());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setAuditUserId(p.getAuditUserId());
            vo.setAuditUserName(p.getAuditUserName());
            vo.setAuditTime(p.getAuditTime());
            Optional.ofNullable(p.getAuditTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setAuditTimeAgo);
            vo.setAuditReason(p.getAuditReason());
            vo.setExecUserId(p.getCreateUserId());
            vo.setExecUserName(p.getCreateUserName());
            vo.setCreateTime(p.getCreateTime());
            Optional.ofNullable(p.getCreateTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setCreateTimeAgo);
            Date startTime = p.getExecStartTime();
            Date endTime = p.getExecEndTime();
            vo.setExecStartTime(startTime);
            vo.setExecEndTime(endTime);
            Optional.ofNullable(startTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setExecStartTimeAgo);
            Optional.ofNullable(endTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setExecEndTimeAgo);
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDO.class, ApplicationPipelineTaskListVO.class, p -> {
            ApplicationPipelineTaskListVO vo = new ApplicationPipelineTaskListVO();
            vo.setId(p.getId());
            vo.setPipelineId(p.getPipelineId());
            vo.setPipelineName(p.getPipelineName());
            vo.setTitle(p.getExecTitle());
            vo.setDescription(p.getExecDescription());
            vo.setStatus(p.getExecStatus());
            vo.setTimedExec(p.getTimedExec());
            vo.setTimedExecTime(p.getTimedExecTime());
            vo.setCreateUserId(p.getCreateUserId());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setExecUserId(p.getExecUserId());
            vo.setExecUserName(p.getExecUserName());
            vo.setCreateTime(p.getCreateTime());
            Date startTime = p.getExecStartTime();
            Date endTime = p.getExecEndTime();
            vo.setExecStartTime(startTime);
            vo.setExecEndTime(endTime);
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDetailDO.class, ApplicationPipelineTaskDetailVO.class, p -> {
            ApplicationPipelineTaskDetailVO vo = new ApplicationPipelineTaskDetailVO();
            vo.setId(p.getId());
            vo.setTaskId(p.getTaskId());
            vo.setAppId(p.getAppId());
            vo.setRelId(p.getRelId());
            vo.setAppName(p.getAppName());
            vo.setAppTag(p.getAppTag());
            vo.setStageType(p.getStageType());
            ApplicationPipelineStageConfigDTO config = JSON.parseObject(p.getStageConfig(), ApplicationPipelineStageConfigDTO.class);
            vo.setConfig(Converts.to(config, ApplicationPipelineStageConfigVO.class));
            vo.setStatus(p.getExecStatus());
            Date startTime = p.getExecStartTime();
            Date endTime = p.getExecEndTime();
            vo.setStartTime(startTime);
            vo.setEndTime(endTime);
            Optional.ofNullable(startTime).map(Dates::ago).ifPresent(vo::setStartTimeAgo);
            Optional.ofNullable(endTime).map(Dates::ago).ifPresent(vo::setEndTimeAgo);
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDetailDO.class, ApplicationPipelineTaskDetailStatusVO.class, p -> {
            ApplicationPipelineTaskDetailStatusVO vo = new ApplicationPipelineTaskDetailStatusVO();
            vo.setId(p.getId());
            vo.setTaskId(p.getTaskId());
            vo.setRelId(p.getRelId());
            vo.setStatus(p.getExecStatus());
            Date startTime = p.getExecStartTime();
            Date endTime = p.getExecEndTime();
            vo.setStartTime(startTime);
            vo.setEndTime(endTime);
            Optional.ofNullable(startTime).map(Dates::ago).ifPresent(vo::setStartTimeAgo);
            Optional.ofNullable(endTime).map(Dates::ago).ifPresent(vo::setEndTimeAgo);
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationPipelineStageConfigDTO.class, ApplicationPipelineStageConfigVO.class, p -> {
            ApplicationPipelineStageConfigVO dto = new ApplicationPipelineStageConfigVO();
            dto.setBranchName(p.getBranchName());
            dto.setCommitId(p.getCommitId());
            dto.setBuildId(p.getBuildId());
            dto.setMachineIdList(p.getMachineIdList());
            return dto;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationPipelineStageConfigDTO.class, ApplicationPipelineTaskDetailRequest.class, p -> {
            ApplicationPipelineTaskDetailRequest req = new ApplicationPipelineTaskDetailRequest();
            req.setBranchName(p.getBranchName());
            req.setCommitId(p.getCommitId());
            req.setBuildId(p.getBuildId());
            req.setTitle(p.getTitle());
            req.setDescription(p.getDescription());
            req.setMachineIdList(p.getMachineIdList());
            return req;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskStatisticsDTO.class, ApplicationPipelineTaskStatisticsChartVO.class, p -> {
            ApplicationPipelineTaskStatisticsChartVO vo = new ApplicationPipelineTaskStatisticsChartVO();
            vo.setDate(Dates.format(p.getDate(), Dates.YMD));
            vo.setExecCount(p.getExecCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskStatisticsDTO.class, ApplicationPipelineTaskStatisticsMetricsVO.class, p -> {
            ApplicationPipelineTaskStatisticsMetricsVO vo = new ApplicationPipelineTaskStatisticsMetricsVO();
            vo.setExecCount(p.getExecCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            vo.setAvgUsed(p.getAvgUsed());
            vo.setAvgUsedInterval(Utils.interval(p.getAvgUsed()));
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDO.class, ApplicationPipelineTaskStatusVO.class, p -> {
            ApplicationPipelineTaskStatusVO vo = new ApplicationPipelineTaskStatusVO();
            vo.setId(p.getId());
            vo.setStatus(p.getExecStatus());
            Date startTime = p.getExecStartTime(), endTime = p.getExecEndTime();
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
        TypeStore.STORE.register(ApplicationPipelineTaskDetailDO.class, ApplicationPipelineTaskStatisticsDetailVO.class, p -> {
            ApplicationPipelineTaskStatisticsDetailVO vo = new ApplicationPipelineTaskStatisticsDetailVO();
            vo.setId(p.getId());
            vo.setRelId(p.getRelId());
            vo.setStageType(p.getStageType());
            vo.setStatus(p.getExecStatus());
            Date startTime = p.getExecStartTime();
            Date endTime = p.getExecEndTime();
            if (startTime != null && endTime != null) {
                long used = endTime.getTime() - startTime.getTime();
                vo.setUsed(used);
                vo.setUsedInterval(Utils.interval(used));
            }
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskLogDO.class, ApplicationPipelineTaskLogVO.class, p -> {
            ApplicationPipelineTaskLogVO vo = new ApplicationPipelineTaskLogVO();
            vo.setId(p.getId());
            vo.setTaskId(p.getTaskId());
            vo.setTaskDetailId(p.getTaskDetailId());
            vo.setStatus(p.getLogStatus());
            vo.setType(p.getStageType());
            vo.setLog(p.getLogInfo());
            vo.setCreateTime(p.getCreateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            return vo;
        });
    }

}
