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
package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.ops.entity.request.app.ApplicationBuildRequest;
import com.orion.ops.entity.vo.app.ApplicationBuildReleaseListVO;
import com.orion.ops.entity.vo.app.ApplicationBuildStatusVO;
import com.orion.ops.entity.vo.app.ApplicationBuildVO;

import java.util.List;

/**
 * 应用构建服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/26 23:23
 */
public interface ApplicationBuildService {

    /**
     * 提交执行
     *
     * @param request request
     * @param execute 是否提交任务
     * @return id
     */
    Long submitBuildTask(ApplicationBuildRequest request, boolean execute);

    /**
     * 获取构建列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<ApplicationBuildVO> getBuildList(ApplicationBuildRequest request);

    /**
     * 获取构建详情
     *
     * @param id id
     * @return row
     */
    ApplicationBuildVO getBuildDetail(Long id);

    /**
     * 查询构建状态
     *
     * @param id id
     * @return status
     */
    ApplicationBuildStatusVO getBuildStatus(Long id);

    /**
     * 查询构建状态列表
     *
     * @param buildIdList id
     * @return key: id  value: status
     */
    List<ApplicationBuildStatusVO> getBuildStatusList(List<Long> buildIdList);

    /**
     * 停止构建
     *
     * @param id id
     */
    void terminateBuildTask(Long id);

    /**
     * 输入命令
     *
     * @param id      id
     * @param command command
     */
    void writeBuildTask(Long id, String command);

    /**
     * 删除构建
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteBuildTask(List<Long> idList);

    /**
     * 重新构建
     *
     * @param id id
     * @return id
     */
    Long rebuild(Long id);

    /**
     * 通过id查询
     *
     * @param id id
     * @return row
     */
    ApplicationBuildDO selectById(Long id);

    /**
     * 获取构建日志路径
     *
     * @param id id
     * @return path
     */
    String getBuildLogPath(Long id);

    /**
     * 获取构建产物路径
     *
     * @param id id
     * @return path
     */
    String getBuildBundlePath(Long id);

    /**
     * 检查并且获取构建目录
     *
     * @param build build
     * @return 构建产物路径
     */
    String checkBuildBundlePath(ApplicationBuildDO build);

    /**
     * 获取构建发布序列
     *
     * @param appId     appId
     * @param profileId profileId
     * @return rows
     */
    List<ApplicationBuildReleaseListVO> getBuildReleaseList(Long appId, Long profileId);

}
