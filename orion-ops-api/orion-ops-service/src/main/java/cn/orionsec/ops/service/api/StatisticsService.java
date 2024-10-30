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
package cn.orionsec.ops.service.api;

import cn.orionsec.ops.entity.request.home.HomeStatisticsRequest;
import cn.orionsec.ops.entity.vo.app.*;
import cn.orionsec.ops.entity.vo.home.HomeStatisticsVO;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskRecordStatisticsVO;

import java.util.List;

/**
 * 统计service
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/20 14:13
 */
public interface StatisticsService {

    /**
     * 获取首页统计信息
     *
     * @param request request
     * @return 统计信息
     */
    HomeStatisticsVO homeStatistics(HomeStatisticsRequest request);

    /**
     * 获取调度任务统计
     *
     * @param taskId 任务id
     * @return 统计
     */
    SchedulerTaskRecordStatisticsVO schedulerTaskStatistics(Long taskId);

    /**
     * 获取应用构建统计 视图
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 视图
     */
    ApplicationBuildStatisticsViewVO appBuildStatisticsView(Long appId, Long profileId);

    /**
     * 获取应用构建统计 指标
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 指标
     */
    ApplicationBuildStatisticsMetricsWrapperVO appBuildStatisticsMetrics(Long appId, Long profileId);

    /**
     * 获取应用构建统计 表格
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 折线图
     */
    List<ApplicationBuildStatisticsChartVO> appBuildStatisticsChart(Long appId, Long profileId);

    /**
     * 获取应用发布统计 视图
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 视图
     */
    ApplicationReleaseStatisticsViewVO appReleaseStatisticView(Long appId, Long profileId);

    /**
     * 获取应用发布统计 指标
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 指标
     */
    ApplicationReleaseStatisticsMetricsWrapperVO appReleaseStatisticMetrics(Long appId, Long profileId);

    /**
     * 获取应用发布统计 折线图
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 折线图
     */
    List<ApplicationReleaseStatisticsChartVO> appReleaseStatisticChart(Long appId, Long profileId);

    /**
     * 获取应用发布统计 视图
     *
     * @param pipelineId pipelineId
     * @return 视图
     */
    ApplicationPipelineTaskStatisticsViewVO appPipelineTaskStatisticView(Long pipelineId);

    /**
     * 获取应用发布统计 指标
     *
     * @param pipelineId pipelineId
     * @return 指标
     */
    ApplicationPipelineTaskStatisticsMetricsWrapperVO appPipelineTaskStatisticMetrics(Long pipelineId);

    /**
     * 获取应用发布统计 折线图
     *
     * @param pipelineId pipelineId
     * @return 折线图
     */
    List<ApplicationPipelineTaskStatisticsChartVO> appPipelineTaskStatisticChart(Long pipelineId);

}
