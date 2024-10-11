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
package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ext.vcs.git.Gits;
import com.orion.ext.vcs.git.info.BranchInfo;
import com.orion.ext.vcs.git.info.LogInfo;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Arrays1;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.Threads;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.convert.Converts;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.io.Streams;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.app.RepositoryAuthType;
import com.orion.ops.constant.app.RepositoryStatus;
import com.orion.ops.constant.app.RepositoryTokenType;
import com.orion.ops.constant.app.RepositoryType;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.constant.message.MessageType;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.dao.ApplicationBuildDAO;
import com.orion.ops.dao.ApplicationInfoDAO;
import com.orion.ops.dao.ApplicationRepositoryDAO;
import com.orion.ops.entity.domain.ApplicationRepositoryDO;
import com.orion.ops.entity.dto.user.UserDTO;
import com.orion.ops.entity.request.app.ApplicationRepositoryRequest;
import com.orion.ops.entity.vo.app.ApplicationRepositoryBranchVO;
import com.orion.ops.entity.vo.app.ApplicationRepositoryCommitVO;
import com.orion.ops.entity.vo.app.ApplicationRepositoryInfoVO;
import com.orion.ops.entity.vo.app.ApplicationRepositoryVO;
import com.orion.ops.service.api.ApplicationRepositoryService;
import com.orion.ops.service.api.WebSideMessageService;
import com.orion.ops.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * 应用仓库服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/28 21:27
 */
@Slf4j
@Service("applicationRepositoryService")
public class ApplicationRepositoryServiceImpl implements ApplicationRepositoryService {

    @Resource
    private ApplicationRepositoryDAO applicationRepositoryDAO;

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private ApplicationInfoDAO applicationInfoDAO;

    @Resource
    private WebSideMessageService webSideMessageService;

    @Override
    public Long addRepository(ApplicationRepositoryRequest request) {
        // 检查名称是否存在
        this.checkNamePresent(null, request.getName());
        // 插入
        ApplicationRepositoryDO insert = new ApplicationRepositoryDO();
        insert.setRepoName(request.getName());
        insert.setRepoDescription(request.getDescription());
        insert.setRepoType(RepositoryType.GIT.getType());
        insert.setRepoUrl(request.getUrl());
        insert.setRepoUsername(request.getUsername());
        insert.setRepoAuthType(request.getAuthType());
        insert.setRepoTokenType(request.getTokenType());
        // 加密密码
        String password = request.getPassword();
        if (!Strings.isBlank(password)) {
            password = ValueMix.encrypt(password);
            insert.setRepoPassword(password);
        }
        // 加密token
        String token = request.getPrivateToken();
        if (!Strings.isBlank(token)) {
            token = ValueMix.encrypt(token);
            insert.setRepoPrivateToken(token);
        }
        insert.setRepoStatus(RepositoryStatus.UNINITIALIZED.getStatus());
        applicationRepositoryDAO.insert(insert);
        // 设置日志参数
        EventParamsHolder.addParams(insert);
        return insert.getId();
    }

    @Override
    public Integer updateRepository(ApplicationRepositoryRequest request) {
        Long id = request.getId();
        // 检查名称是否存在
        this.checkNamePresent(id, request.getName());
        // 查询修改前的数据
        ApplicationRepositoryDO beforeRepo = applicationRepositoryDAO.selectById(id);
        Valid.notNull(beforeRepo, MessageConst.UNKNOWN_DATA);
        // 更新
        ApplicationRepositoryDO update = new ApplicationRepositoryDO();
        update.setId(id);
        update.setRepoName(request.getName());
        update.setRepoDescription(request.getDescription());
        update.setRepoUrl(request.getUrl());
        update.setRepoUsername(request.getUsername());
        update.setRepoAuthType(request.getAuthType());
        update.setRepoTokenType(request.getTokenType());
        // 加密密码
        String password = request.getPassword();
        if (!Strings.isBlank(password)) {
            password = ValueMix.encrypt(password);
            update.setRepoPassword(password);
        }
        // 加密token
        String token = request.getPrivateToken();
        if (!Strings.isBlank(token)) {
            token = ValueMix.encrypt(token);
            update.setRepoPrivateToken(token);
        }
        if (!beforeRepo.getRepoUrl().equals(update.getRepoUrl())) {
            // 如果修改了url则状态改为未初始化
            update.setRepoStatus(RepositoryStatus.UNINITIALIZED.getStatus());
            // 删除 event 目录
            File clonePath = new File(Utils.getRepositoryEventDir(id));
            Files1.delete(clonePath);
        }
        int effect = applicationRepositoryDAO.updateById(update);
        // 设置日志参数
        EventParamsHolder.addParams(update);
        EventParamsHolder.addParam(EventKeys.NAME, beforeRepo.getRepoName());
        return effect;
    }

    @Override
    public Integer deleteRepository(Long id) {
        ApplicationRepositoryDO beforeRepo = applicationRepositoryDAO.selectById(id);
        Valid.notNull(beforeRepo, MessageConst.UNKNOWN_DATA);
        // 清空app引用
        applicationInfoDAO.cleanRepoCount(id);
        // 删除
        int effect = applicationRepositoryDAO.deleteById(id);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, beforeRepo.getRepoName());
        return effect;
    }

    @Override
    public DataGrid<ApplicationRepositoryVO> listRepository(ApplicationRepositoryRequest request) {
        LambdaQueryWrapper<ApplicationRepositoryDO> wrapper = new LambdaQueryWrapper<ApplicationRepositoryDO>()
                .like(Objects.nonNull(request.getName()), ApplicationRepositoryDO::getRepoName, request.getName())
                .like(Objects.nonNull(request.getDescription()), ApplicationRepositoryDO::getRepoDescription, request.getDescription())
                .like(Objects.nonNull(request.getUrl()), ApplicationRepositoryDO::getRepoUrl, request.getUrl())
                .like(Objects.nonNull(request.getUsername()), ApplicationRepositoryDO::getRepoUsername, request.getUsername())
                .eq(Objects.nonNull(request.getId()), ApplicationRepositoryDO::getId, request.getId())
                .eq(Objects.nonNull(request.getType()), ApplicationRepositoryDO::getRepoType, request.getType())
                .eq(Objects.nonNull(request.getStatus()), ApplicationRepositoryDO::getRepoStatus, request.getStatus())
                .orderByAsc(ApplicationRepositoryDO::getId);
        return DataQuery.of(applicationRepositoryDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(ApplicationRepositoryVO.class);
    }

    @Override
    public ApplicationRepositoryVO getRepositoryDetail(Long id) {
        ApplicationRepositoryDO repo = applicationRepositoryDAO.selectById(id);
        Valid.notNull(repo, MessageConst.UNKNOWN_DATA);
        return Converts.to(repo, ApplicationRepositoryVO.class);
    }

    @Override
    public void initEventRepository(Long id, boolean isReInit) {
        // 查询数据
        ApplicationRepositoryDO repo = applicationRepositoryDAO.selectById(id);
        Valid.notNull(repo, MessageConst.UNKNOWN_DATA);
        // 判断状态
        if (RepositoryStatus.INITIALIZING.getStatus().equals(repo.getRepoStatus())) {
            throw Exceptions.runtime(MessageConst.REPO_INITIALIZING);
        } else if (RepositoryStatus.OK.getStatus().equals(repo.getRepoStatus()) && !isReInit) {
            throw Exceptions.runtime(MessageConst.REPO_INITIALIZED);
        } else if (!RepositoryStatus.OK.getStatus().equals(repo.getRepoStatus()) && isReInit) {
            throw Exceptions.runtime(MessageConst.REPO_UNINITIALIZED);
        }
        // 获取当前用户
        UserDTO user = Currents.getUser();
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, repo.getRepoName());
        // 修改状态
        ApplicationRepositoryDO update = new ApplicationRepositoryDO();
        update.setId(id);
        update.setRepoStatus(RepositoryStatus.INITIALIZING.getStatus());
        applicationRepositoryDAO.updateById(update);
        // 提交线程异步处理
        log.info("开始初始化应用仓库 id: {}", id);
        Threads.start(() -> {
            // 删除
            File clonePath = new File(Utils.getRepositoryEventDir(id));
            Files1.delete(clonePath);
            // 初始化
            Exception ex = null;
            Gits gits = null;
            try {
                // clone
                String[] pair = this.getRepositoryUsernamePassword(repo);
                String username = pair[0];
                String password = pair[1];
                if (username == null) {
                    gits = Gits.clone(repo.getRepoUrl(), clonePath);
                } else {
                    gits = Gits.clone(repo.getRepoUrl(), clonePath, username, password);
                }
            } catch (Exception e) {
                ex = e;
            } finally {
                Streams.close(gits);
            }
            MessageType message;
            // 修改状态
            if (ex == null) {
                message = MessageType.REPOSITORY_INIT_SUCCESS;
                update.setRepoStatus(RepositoryStatus.OK.getStatus());
            } else {
                Files1.delete(clonePath);
                message = MessageType.REPOSITORY_INIT_FAILURE;
                update.setRepoStatus(RepositoryStatus.ERROR.getStatus());
            }
            applicationRepositoryDAO.updateById(update);
            // 发送站内信
            Map<String, Object> params = Maps.newMap();
            params.put(EventKeys.ID, repo.getId());
            params.put(EventKeys.NAME, repo.getRepoName());
            webSideMessageService.addMessage(message, repo.getId(), user.getId(), user.getUsername(), params);
            if (ex == null) {
                log.info("应用仓库初始化成功 id: {}", id);
            } else {
                log.error("应用仓库初始化失败 id: {}", id, ex);
                throw Exceptions.argument(MessageConst.REPO_INIT_ERROR, ex);
            }
        });
    }

    @Override
    public ApplicationRepositoryInfoVO getRepositoryInfo(ApplicationRepositoryRequest request) {
        Long id = request.getId();
        // 打开git
        try (Gits gits = this.openEventGit(id)) {
            gits.fetch();
            ApplicationRepositoryInfoVO repsInfo = new ApplicationRepositoryInfoVO();
            ApplicationRepositoryBranchVO defaultBranch;
            // 获取分支列表
            List<ApplicationRepositoryBranchVO> branches = Converts.toList(gits.branchList(), ApplicationRepositoryBranchVO.class);
            // 获取当前环境上次构建分支
            String lastBranchName = applicationBuildDAO.selectLastBuildBranch(request.getAppId(), request.getProfileId(), id);
            if (lastBranchName != null) {
                defaultBranch = branches.stream()
                        .filter(s -> s.getName().equals(lastBranchName))
                        .findFirst()
                        .orElseGet(() -> branches.get(branches.size() - 1));
            } else {
                defaultBranch = branches.get(branches.size() - 1);
            }
            defaultBranch.setIsDefault(Const.IS_DEFAULT);
            repsInfo.setBranches(branches);
            // 获取commit
            try {
                List<LogInfo> logList = gits.logList(defaultBranch.getName(), Const.COMMIT_LIMIT);
                repsInfo.setCommits(Converts.toList(logList, ApplicationRepositoryCommitVO.class));
            } catch (Exception e) {
                log.error("获取repo-commit列表失败: id: {}, branch: {}, e: {}", id, defaultBranch.getName(), e);
                throw e;
            }
            return repsInfo;
        }
    }

    @Override
    public List<ApplicationRepositoryBranchVO> getRepositoryBranchList(Long id) {
        // 打开git
        try (Gits gits = this.openEventGit(id)) {
            gits.fetch();
            // 查询分支信息
            List<BranchInfo> branchList = gits.branchList();
            return Converts.toList(branchList, ApplicationRepositoryBranchVO.class);
        }
    }

    @Override
    public List<ApplicationRepositoryCommitVO> getRepositoryCommitList(Long id, String branchName) {
        // 打开git
        try (Gits gits = this.openEventGit(id)) {
            gits.fetch(branchName.split("/")[0]);
            // 查询提交信息
            List<LogInfo> logList = gits.logList(branchName, Const.COMMIT_LIMIT);
            return Converts.toList(logList, ApplicationRepositoryCommitVO.class);
        } catch (Exception e) {
            log.error("获取repo-commit列表失败: id: {}, branch: {}", id, branchName, e);
            throw e;
        }
    }

    @Override
    public Gits openEventGit(Long id) {
        // 查询数据
        ApplicationRepositoryDO repo = applicationRepositoryDAO.selectById(id);
        Valid.notNull(repo, MessageConst.UNKNOWN_DATA);
        Valid.isTrue(RepositoryStatus.OK.getStatus().equals(repo.getRepoStatus()), MessageConst.REPO_UNINITIALIZED);
        // 获取仓库位置
        File repoPath = new File(Utils.getRepositoryEventDir(id));
        if (!repoPath.isDirectory()) {
            // 修改状态为未初始化
            ApplicationRepositoryDO entity = new ApplicationRepositoryDO();
            entity.setId(id);
            entity.setRepoStatus(RepositoryStatus.UNINITIALIZED.getStatus());
            applicationRepositoryDAO.updateById(entity);
            throw Exceptions.argument(MessageConst.REPO_PATH_ABSENT, Exceptions.runtime(repoPath.getAbsolutePath()));
        }
        // 打开git
        try {
            Gits gits = Gits.of(repoPath);
            String[] pair = this.getRepositoryUsernamePassword(repo);
            String username = pair[0];
            String password = pair[1];
            if (username != null) {
                gits.auth(username, password);
            }
            return gits;
        } catch (Exception e) {
            throw Exceptions.runtime(MessageConst.REPO_UNABLE_CONNECT, e);
        }
    }

    @Override
    public void cleanBuildRepository(Long id) {
        // 查询数据
        ApplicationRepositoryDO repo = applicationRepositoryDAO.selectById(id);
        Valid.notNull(repo, MessageConst.UNKNOWN_DATA);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, repo.getRepoName());
        File rootPath = new File(Files1.getPath(SystemEnvAttr.REPO_PATH.getValue(), id + Const.EMPTY));
        if (!Files1.isDirectory(rootPath)) {
            return;
        }
        // 查询文件夹
        File[] files = rootPath.listFiles(e -> !e.getName().equals(Const.EVENT)
                && e.isDirectory()
                && Strings.isInteger(e.getName()));
        if (Arrays1.isEmpty(files)) {
            return;
        }
        // 保留两个版本 防止清空正在进行中的构建任务
        int length = files.length;
        if (length <= 2) {
            return;
        }
        Arrays.sort(files, Comparator.comparing(s -> Integer.parseInt(s.getName())));
        for (int i = 0; i < length - 2; i++) {
            Files1.delete(files[i]);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncRepositoryStatus() {
        List<ApplicationRepositoryDO> repoList = applicationRepositoryDAO.selectList(new LambdaQueryWrapper<>());
        for (ApplicationRepositoryDO repo : repoList) {
            Long id = repo.getId();
            File repoPath = new File(Utils.getRepositoryEventDir(id));
            boolean isDir = Files1.isDirectory(repoPath);
            // 更新状态
            ApplicationRepositoryDO update = new ApplicationRepositoryDO();
            update.setId(id);
            update.setRepoStatus(isDir ? RepositoryStatus.OK.getStatus() : RepositoryStatus.UNINITIALIZED.getStatus());
            update.setUpdateTime(new Date());
            applicationRepositoryDAO.updateById(update);
        }
    }

    @Override
    public ApplicationRepositoryDO selectById(Long id) {
        return applicationRepositoryDAO.selectById(id);
    }

    @Override
    public String[] getRepositoryUsernamePassword(ApplicationRepositoryDO repository) {
        String username = null;
        String password = null;
        RepositoryAuthType authType = RepositoryAuthType.of(repository.getRepoAuthType());
        if (RepositoryAuthType.PASSWORD.equals(authType)) {
            // 用户名
            String repoUsername = repository.getRepoUsername();
            if (!Strings.isBlank(repoUsername)) {
                username = repoUsername;
                password = ValueMix.decrypt(repository.getRepoPassword());
            }
        } else {
            // token
            RepositoryTokenType tokenType = RepositoryTokenType.of(repository.getRepoTokenType());
            switch (tokenType) {
                case GITHUB:
                    username = Const.EMPTY;
                    break;
                case GITEE:
                    username = repository.getRepoUsername();
                    break;
                case GITLAB:
                    username = Const.OAUTH2;
                    break;
                default:
                    break;
            }
            password = ValueMix.decrypt(repository.getRepoPrivateToken());
        }
        return new String[]{username, password};
    }

    /**
     * 检查是否存在
     *
     * @param id   id
     * @param name name
     */
    private void checkNamePresent(Long id, String name) {
        LambdaQueryWrapper<ApplicationRepositoryDO> presentWrapper = new LambdaQueryWrapper<ApplicationRepositoryDO>()
                .ne(id != null, ApplicationRepositoryDO::getId, id)
                .eq(ApplicationRepositoryDO::getRepoName, name);
        boolean present = DataQuery.of(applicationRepositoryDAO).wrapper(presentWrapper).present();
        Valid.isTrue(!present, MessageConst.NAME_PRESENT);
    }

}
