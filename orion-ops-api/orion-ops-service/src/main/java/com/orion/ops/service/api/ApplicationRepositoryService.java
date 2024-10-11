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

import com.orion.ext.vcs.git.Gits;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.entity.domain.ApplicationRepositoryDO;
import com.orion.ops.entity.request.app.ApplicationRepositoryRequest;
import com.orion.ops.entity.vo.app.ApplicationRepositoryBranchVO;
import com.orion.ops.entity.vo.app.ApplicationRepositoryCommitVO;
import com.orion.ops.entity.vo.app.ApplicationRepositoryInfoVO;
import com.orion.ops.entity.vo.app.ApplicationRepositoryVO;

import java.util.List;

/**
 * 应用版本仓库仓库服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/26 23:22
 */
public interface ApplicationRepositoryService {

    /**
     * 添加仓库
     *
     * @param request request
     * @return id
     */
    Long addRepository(ApplicationRepositoryRequest request);

    /**
     * 更新仓库
     *
     * @param request request
     * @return effect
     */
    Integer updateRepository(ApplicationRepositoryRequest request);

    /**
     * 通过 id 删除
     *
     * @param id id
     * @return effect
     */
    Integer deleteRepository(Long id);

    /**
     * 获取列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<ApplicationRepositoryVO> listRepository(ApplicationRepositoryRequest request);

    /**
     * 获取详情
     *
     * @param id id
     * @return row
     */
    ApplicationRepositoryVO getRepositoryDetail(Long id);

    /**
     * 初始化 event 仓库
     *
     * @param id       id
     * @param isReInit 是否是重新初始化
     * @see #getRepositoryInfo
     * @see #getRepositoryBranchList
     * @see #getRepositoryCommitList
     */
    void initEventRepository(Long id, boolean isReInit);

    /**
     * 获取版本信息列表
     *
     * @param request request
     * @return 分支信息
     */
    ApplicationRepositoryInfoVO getRepositoryInfo(ApplicationRepositoryRequest request);

    /**
     * 获取分支列表
     *
     * @param id id
     * @return 分支信息
     */
    List<ApplicationRepositoryBranchVO> getRepositoryBranchList(Long id);

    /**
     * 获取提交列表
     *
     * @param id         id
     * @param branchName 分支名称
     * @return log
     */
    List<ApplicationRepositoryCommitVO> getRepositoryCommitList(Long id, String branchName);

    /**
     * 打开事件git
     *
     * @param id id
     * @return gits
     */
    Gits openEventGit(Long id);

    /**
     * 清空仓库
     *
     * @param id id
     */
    void cleanBuildRepository(Long id);

    /**
     * 同步仓库状态
     */
    void syncRepositoryStatus();

    /**
     * 查询仓库
     *
     * @param id id
     * @return 仓库
     */
    ApplicationRepositoryDO selectById(Long id);

    /**
     * 获取仓库账号密码
     *
     * @param repository repository
     * @return [username, password]
     */
    String[] getRepositoryUsernamePassword(ApplicationRepositoryDO repository);

}
