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
import com.orion.ops.constant.app.VcsAuthType;
import com.orion.ops.constant.app.VcsStatus;
import com.orion.ops.constant.app.VcsTokenType;
import com.orion.ops.constant.app.VcsType;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.constant.event.EventParamsHolder;
import com.orion.ops.constant.message.MessageType;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.dao.ApplicationBuildDAO;
import com.orion.ops.dao.ApplicationInfoDAO;
import com.orion.ops.dao.ApplicationVcsDAO;
import com.orion.ops.entity.domain.ApplicationVcsDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.ApplicationVcsRequest;
import com.orion.ops.entity.vo.ApplicationVcsBranchVO;
import com.orion.ops.entity.vo.ApplicationVcsCommitVO;
import com.orion.ops.entity.vo.ApplicationVcsInfoVO;
import com.orion.ops.entity.vo.ApplicationVcsVO;
import com.orion.ops.service.api.ApplicationVcsService;
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
@Service("applicationVcsService")
public class ApplicationVcsServiceImpl implements ApplicationVcsService {

    @Resource
    private ApplicationVcsDAO applicationVcsDAO;

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private ApplicationInfoDAO applicationInfoDAO;

    @Resource
    private WebSideMessageService webSideMessageService;

    @Override
    public Long addAppVcs(ApplicationVcsRequest request) {
        // 检查名称是否存在
        this.checkNamePresent(null, request.getName());
        // 插入
        ApplicationVcsDO insert = new ApplicationVcsDO();
        insert.setVcsName(request.getName());
        insert.setVcsDescription(request.getDescription());
        insert.setVcsType(VcsType.GIT.getType());
        insert.setVscUrl(request.getUrl());
        insert.setVscUsername(request.getUsername());
        insert.setVcsAuthType(request.getAuthType());
        insert.setVcsTokenType(request.getTokenType());
        // 加密密码
        String password = request.getPassword();
        if (!Strings.isBlank(password)) {
            password = ValueMix.encrypt(password);
            insert.setVcsPassword(password);
        }
        // 加密token
        String token = request.getPrivateToken();
        if (!Strings.isBlank(token)) {
            token = ValueMix.encrypt(token);
            insert.setVcsPrivateToken(token);
        }
        insert.setVcsStatus(VcsStatus.UNINITIALIZED.getStatus());
        applicationVcsDAO.insert(insert);
        // 设置日志参数
        EventParamsHolder.addParams(insert);
        return insert.getId();
    }

    @Override
    public Integer updateAppVcs(ApplicationVcsRequest request) {
        Long id = request.getId();
        // 检查名称是否存在
        this.checkNamePresent(id, request.getName());
        // 查询修改前的数据
        ApplicationVcsDO beforeVcs = applicationVcsDAO.selectById(id);
        Valid.notNull(beforeVcs, MessageConst.UNKNOWN_DATA);
        // 更新
        ApplicationVcsDO update = new ApplicationVcsDO();
        update.setId(id);
        update.setVcsName(request.getName());
        update.setVcsDescription(request.getDescription());
        update.setVscUrl(request.getUrl());
        update.setVscUsername(request.getUsername());
        update.setVcsAuthType(request.getAuthType());
        update.setVcsTokenType(request.getTokenType());
        // 加密密码
        String password = request.getPassword();
        if (!Strings.isBlank(password)) {
            password = ValueMix.encrypt(password);
            update.setVcsPassword(password);
        }
        // 加密token
        String token = request.getPrivateToken();
        if (!Strings.isBlank(token)) {
            token = ValueMix.encrypt(token);
            update.setVcsPrivateToken(token);
        }
        if (!beforeVcs.getVscUrl().equals(update.getVscUrl())) {
            // 如果修改了url则状态改为未初始化
            update.setVcsStatus(VcsStatus.UNINITIALIZED.getStatus());
            // 删除 event 目录
            File clonePath = new File(Utils.getVcsEventDir(id));
            Files1.delete(clonePath);
        }
        int effect = applicationVcsDAO.updateById(update);
        // 设置日志参数
        EventParamsHolder.addParams(update);
        EventParamsHolder.addParam(EventKeys.NAME, beforeVcs.getVcsName());
        return effect;
    }

    @Override
    public Integer deleteAppVcs(Long id) {
        ApplicationVcsDO beforeVcs = applicationVcsDAO.selectById(id);
        Valid.notNull(beforeVcs, MessageConst.UNKNOWN_DATA);
        // 清空app引用
        applicationInfoDAO.cleanVcsCount(id);
        // 删除
        int effect = applicationVcsDAO.deleteById(id);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, beforeVcs.getVcsName());
        return effect;
    }

    @Override
    public DataGrid<ApplicationVcsVO> listAppVcs(ApplicationVcsRequest request) {
        LambdaQueryWrapper<ApplicationVcsDO> wrapper = new LambdaQueryWrapper<ApplicationVcsDO>()
                .like(Objects.nonNull(request.getName()), ApplicationVcsDO::getVcsName, request.getName())
                .like(Objects.nonNull(request.getDescription()), ApplicationVcsDO::getVcsDescription, request.getDescription())
                .like(Objects.nonNull(request.getUrl()), ApplicationVcsDO::getVscUrl, request.getUrl())
                .like(Objects.nonNull(request.getUsername()), ApplicationVcsDO::getVscUsername, request.getUsername())
                .eq(Objects.nonNull(request.getType()), ApplicationVcsDO::getVcsType, request.getType())
                .eq(Objects.nonNull(request.getStatus()), ApplicationVcsDO::getVcsStatus, request.getStatus())
                .orderByAsc(ApplicationVcsDO::getId);
        return DataQuery.of(applicationVcsDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(ApplicationVcsVO.class);
    }

    @Override
    public ApplicationVcsVO getAppVcsDetail(Long id) {
        ApplicationVcsDO vcs = applicationVcsDAO.selectById(id);
        Valid.notNull(vcs, MessageConst.UNKNOWN_DATA);
        return Converts.to(vcs, ApplicationVcsVO.class);
    }

    @Override
    public void initEventVcs(Long id, boolean isReInit) {
        // 查询数据
        ApplicationVcsDO vcs = applicationVcsDAO.selectById(id);
        Valid.notNull(vcs, MessageConst.UNKNOWN_DATA);
        // 判断状态
        if (VcsStatus.INITIALIZING.getStatus().equals(vcs.getVcsStatus())) {
            throw Exceptions.runtime(MessageConst.VCS_INITIALIZING);
        } else if (VcsStatus.OK.getStatus().equals(vcs.getVcsStatus()) && !isReInit) {
            throw Exceptions.runtime(MessageConst.VCS_INITIALIZED);
        } else if (!VcsStatus.OK.getStatus().equals(vcs.getVcsStatus()) && isReInit) {
            throw Exceptions.runtime(MessageConst.VCS_UNINITIALIZED);
        }
        // 获取当前用户
        UserDTO user = Currents.getUser();
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, vcs.getVcsName());
        // 修改状态
        ApplicationVcsDO update = new ApplicationVcsDO();
        update.setId(id);
        update.setVcsStatus(VcsStatus.INITIALIZING.getStatus());
        applicationVcsDAO.updateById(update);
        // 提交线程异步处理
        log.info("开始初始化应用仓库 id: {}", id);
        Threads.start(() -> {
            // 删除
            File clonePath = new File(Utils.getVcsEventDir(id));
            Files1.delete(clonePath);
            // 初始化
            Exception ex = null;
            Gits gits = null;
            try {
                // clone
                String[] pair = this.getVcsUsernamePassword(vcs);
                String username = pair[0];
                String password = pair[1];
                if (username == null) {
                    gits = Gits.clone(vcs.getVscUrl(), clonePath);
                } else {
                    gits = Gits.clone(vcs.getVscUrl(), clonePath, username, password);
                }
            } catch (Exception e) {
                ex = e;
            } finally {
                Streams.close(gits);
            }
            MessageType message;
            // 修改状态
            if (ex == null) {
                message = MessageType.VCS_INIT_SUCCESS;
                update.setVcsStatus(VcsStatus.OK.getStatus());
            } else {
                Files1.delete(clonePath);
                message = MessageType.VCS_INIT_FAILURE;
                update.setVcsStatus(VcsStatus.ERROR.getStatus());
            }
            applicationVcsDAO.updateById(update);
            // 发送站内信
            Map<String, Object> params = Maps.newMap();
            params.put(EventKeys.ID, vcs.getId());
            params.put(EventKeys.NAME, vcs.getVcsName());
            webSideMessageService.addMessage(message, user.getId(), user.getUsername(), params);
            if (ex == null) {
                log.info("应用仓库初始化成功 id: {}", id);
            } else {
                log.error("应用仓库初始化失败 id: {}", id, ex);
                throw Exceptions.argument(MessageConst.VCS_INIT_ERROR, ex);
            }
        });
    }

    @Override
    public ApplicationVcsInfoVO getVcsInfo(ApplicationVcsRequest request) {
        Long id = request.getId();
        // 打开git
        try (Gits gits = this.openEventGit(id)) {
            gits.fetch();
            ApplicationVcsInfoVO vcsInfo = new ApplicationVcsInfoVO();
            ApplicationVcsBranchVO defaultBranch;
            // 获取分支列表
            List<ApplicationVcsBranchVO> branches = Converts.toList(gits.branchList(), ApplicationVcsBranchVO.class);
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
            vcsInfo.setBranches(branches);
            // 获取commit
            try {
                List<LogInfo> logList = gits.logList(defaultBranch.getName(), Const.VCS_COMMIT_LIMIT);
                vcsInfo.setCommits(Converts.toList(logList, ApplicationVcsCommitVO.class));
            } catch (Exception e) {
                log.error("获取vcs-commit列表失败: id: {}, branch: {}, e: {}", id, defaultBranch.getName(), e);
                throw e;
            }
            return vcsInfo;
        }
    }

    @Override
    public List<ApplicationVcsBranchVO> getVcsBranchList(Long id) {
        // 打开git
        try (Gits gits = this.openEventGit(id)) {
            gits.fetch();
            // 查询分支信息
            List<BranchInfo> branchList = gits.branchList();
            return Converts.toList(branchList, ApplicationVcsBranchVO.class);
        }
    }

    @Override
    public List<ApplicationVcsCommitVO> getVcsCommitList(Long id, String branchName) {
        // 打开git
        try (Gits gits = this.openEventGit(id)) {
            gits.fetch(branchName.split("/")[0]);
            // 查询提交信息
            List<LogInfo> logList = gits.logList(branchName, Const.VCS_COMMIT_LIMIT);
            return Converts.toList(logList, ApplicationVcsCommitVO.class);
        } catch (Exception e) {
            log.error("获取vcs-commit列表失败: id: {}, branch: {}", id, branchName, e);
            throw e;
        }
    }

    @Override
    public Gits openEventGit(Long id) {
        // 查询数据
        ApplicationVcsDO vcs = applicationVcsDAO.selectById(id);
        Valid.notNull(vcs, MessageConst.UNKNOWN_DATA);
        Valid.isTrue(VcsStatus.OK.getStatus().equals(vcs.getVcsStatus()), MessageConst.VCS_UNINITIALIZED);
        // 获取仓库位置
        File vcsPath = new File(Utils.getVcsEventDir(id));
        if (!vcsPath.isDirectory()) {
            // 修改状态为未初始化
            ApplicationVcsDO entity = new ApplicationVcsDO();
            entity.setId(id);
            entity.setVcsStatus(VcsStatus.UNINITIALIZED.getStatus());
            applicationVcsDAO.updateById(entity);
            throw Exceptions.argument(MessageConst.VCS_PATH_ABSENT, Exceptions.runtime(vcsPath.getAbsolutePath()));
        }
        // 打开git
        try {
            Gits gits = Gits.of(vcsPath);
            String[] pair = this.getVcsUsernamePassword(vcs);
            String username = pair[0];
            String password = pair[1];
            if (username != null) {
                gits.auth(username, password);
            }
            return gits;
        } catch (Exception e) {
            throw Exceptions.runtime(MessageConst.VCS_UNABLE_CONNECT, e);
        }
    }

    @Override
    public void cleanBuildVcs(Long id) {
        // 查询数据
        ApplicationVcsDO vcs = applicationVcsDAO.selectById(id);
        Valid.notNull(vcs, MessageConst.UNKNOWN_DATA);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, vcs.getVcsName());
        File rootPath = new File(Files1.getPath(SystemEnvAttr.VCS_PATH.getValue(), id + Const.EMPTY));
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
    public void syncVcsStatus() {
        List<ApplicationVcsDO> vcsList = applicationVcsDAO.selectList(new LambdaQueryWrapper<>());
        for (ApplicationVcsDO vcs : vcsList) {
            Long id = vcs.getId();
            File vcsPath = new File(Utils.getVcsEventDir(id));
            boolean isDir = Files1.isDirectory(vcsPath);
            // 更新状态
            ApplicationVcsDO update = new ApplicationVcsDO();
            update.setId(id);
            update.setVcsStatus(isDir ? VcsStatus.OK.getStatus() : VcsStatus.UNINITIALIZED.getStatus());
            update.setUpdateTime(new Date());
            applicationVcsDAO.updateById(update);
        }
    }

    @Override
    public ApplicationVcsDO selectById(Long id) {
        return applicationVcsDAO.selectById(id);
    }

    @Override
    public String[] getVcsUsernamePassword(ApplicationVcsDO vcs) {
        String username = null;
        String password = null;
        VcsAuthType authType = VcsAuthType.of(vcs.getVcsAuthType());
        if (VcsAuthType.PASSWORD.equals(authType)) {
            // 用户名
            String vscUsername = vcs.getVscUsername();
            if (!Strings.isBlank(vscUsername)) {
                username = vscUsername;
                password = ValueMix.decrypt(vcs.getVcsPassword());
            }
        } else {
            // token
            VcsTokenType tokenType = VcsTokenType.of(vcs.getVcsTokenType());
            switch (tokenType) {
                case GITHUB:
                    username = Const.EMPTY;
                    break;
                case GITEE:
                    username = vcs.getVscUsername();
                    break;
                case GITLAB:
                    username = Const.OAUTH2;
                    break;
                default:
                    break;
            }
            password = ValueMix.decrypt(vcs.getVcsPrivateToken());
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
        LambdaQueryWrapper<ApplicationVcsDO> presentWrapper = new LambdaQueryWrapper<ApplicationVcsDO>()
                .ne(id != null, ApplicationVcsDO::getId, id)
                .eq(ApplicationVcsDO::getVcsName, name);
        boolean present = DataQuery.of(applicationVcsDAO).wrapper(presentWrapper).present();
        Valid.isTrue(!present, MessageConst.NAME_PRESENT);
    }

}
