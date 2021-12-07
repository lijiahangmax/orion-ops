package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.VcsStatus;
import com.orion.ops.consts.app.VcsType;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.ApplicationVcsDAO;
import com.orion.ops.entity.domain.ApplicationVcsDO;
import com.orion.ops.entity.request.ApplicationVcsRequest;
import com.orion.ops.entity.vo.ApplicationVcsBranchVO;
import com.orion.ops.entity.vo.ApplicationVcsCommitVO;
import com.orion.ops.entity.vo.ApplicationVcsInfoVO;
import com.orion.ops.entity.vo.ApplicationVcsVO;
import com.orion.ops.service.api.ApplicationVcsService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.ops.utils.ValueMix;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.vcs.git.Gits;
import com.orion.vcs.git.info.BranchInfo;
import com.orion.vcs.git.info.LogInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Objects;

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
        // 加密密码
        String password = request.getPassword();
        if (!Strings.isBlank(password)) {
            password = ValueMix.encrypt(password);
            insert.setVcsPassword(password);
        }
        insert.setVcsAccessToken(request.getToken());
        insert.setVcsStatus(VcsStatus.UNINITIALIZED.getStatus());
        applicationVcsDAO.insert(insert);
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
        // 加密密码
        String password = request.getPassword();
        if (!Strings.isBlank(password)) {
            password = ValueMix.encrypt(password);
            update.setVcsPassword(password);
        }
        update.setVcsAccessToken(request.getToken());
        // 如果修改了url则状态改为未初始化
        if (!beforeVcs.getVscUrl().equals(update.getVscUrl())) {
            update.setVcsStatus(VcsStatus.UNINITIALIZED.getStatus());
        }
        return applicationVcsDAO.updateById(update);
    }

    @Override
    public Integer deleteAppVcs(Long id) {
        // TODO APP引用检查
        return applicationVcsDAO.deleteById(id);
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
                .orderByDesc(ApplicationVcsDO::getUpdateTime);
        return DataQuery.of(applicationVcsDAO)
                .wrapper(wrapper)
                .page(request)
                .dataGrid(ApplicationVcsVO.class);
    }

    @Override
    public ApplicationVcsVO getAppVcsDetail(Long id) {
        ApplicationVcsDO vcs = applicationVcsDAO.selectById(id);
        Valid.notNull(vcs, MessageConst.UNKNOWN_DATA);
        return Converts.to(vcs, ApplicationVcsVO.class);
    }

    @Override
    public void initVcs(Long id) {
        // 查询数据
        ApplicationVcsDO vcs = applicationVcsDAO.selectById(id);
        Valid.notNull(vcs, MessageConst.UNKNOWN_DATA);
        // 判断状态
        if (VcsStatus.INITIALIZING.getStatus().equals(vcs.getVcsStatus())) {
            throw Exceptions.runtime(MessageConst.VCS_INITIALIZING);
        } else if (VcsStatus.OK.getStatus().equals(vcs.getVcsStatus())) {
            throw Exceptions.runtime(MessageConst.VCS_INITIALIZED);
        }
        // 修改状态
        ApplicationVcsDO update = new ApplicationVcsDO();
        update.setId(id);
        update.setVcsStatus(VcsStatus.INITIALIZING.getStatus());
        applicationVcsDAO.updateById(update);
        // 删除
        File cloneFile = new File(Files1.getPath(MachineEnvAttr.VCS_PATH.getValue(), id + Strings.EMPTY));
        Files1.delete(cloneFile);
        // 初始化
        Exception ex = null;
        Gits gits = null;
        try {
            // 设置地址
            if (cloneFile.isDirectory()) {
                throw Exceptions.argument(MessageConst.VCS_PATH_PRESENT, Exceptions.runtime(cloneFile.getAbsolutePath()));
            }
            // clone
            if (vcs.getVscUsername() == null) {
                gits = Gits.clone(vcs.getVscUrl(), cloneFile);
            } else {
                String username = vcs.getVscUsername();
                String password = ValueMix.decrypt(vcs.getVcsPassword());
                gits = Gits.clone(vcs.getVscUrl(), cloneFile, username, password);
            }
        } catch (Exception e) {
            ex = e;
        } finally {
            Streams.close(gits);
        }
        // 修改状态
        if (ex == null) {
            update.setVcsStatus(VcsStatus.OK.getStatus());
        } else {
            update.setVcsStatus(VcsStatus.ERROR.getStatus());
        }
        applicationVcsDAO.updateById(update);
        if (ex != null) {
            throw Exceptions.argument(MessageConst.VCS_INIT_ERROR, ex);
        }
    }

    @Override
    public ApplicationVcsInfoVO getVcsInfo(Long id) {
        // 打开git
        try (Gits gits = this.openGit(id)) {
            gits.fetch();
            ApplicationVcsInfoVO vcsInfo = new ApplicationVcsInfoVO();
            ApplicationVcsBranchVO branch;
            // 获取分支列表
            List<ApplicationVcsBranchVO> branches = Converts.toList(gits.branchList(), ApplicationVcsBranchVO.class);
            // TODO 获取当前环境上次构建分支
            branch = branches.get(branches.size() - 1);
            branch.setIsDefault(Const.IS_DEFAULT);
            vcsInfo.setBranches(branches);
            // 获取commit
            try {
                List<LogInfo> logList = gits.logList(branch.getName(), Const.VCS_COMMIT_LIMIT);
                vcsInfo.setCommits(Converts.toList(logList, ApplicationVcsCommitVO.class));
            } catch (Exception e) {
                log.error("获取vcs-commit列表失败: id: {}, branch: {}, e: {}", id, branch.getName(), e);
                throw e;
            }
            return vcsInfo;
        }
    }

    @Override
    public List<ApplicationVcsBranchVO> getVcsBranchList(Long id) {
        // 打开git
        try (Gits gits = this.openGit(id)) {
            gits.fetch();
            // 查询分支信息
            List<BranchInfo> branchList = gits.branchList();
            return Converts.toList(branchList, ApplicationVcsBranchVO.class);
        }
    }

    @Override
    public List<ApplicationVcsCommitVO> getVcsCommitList(Long id, String branchName) {
        // 打开git
        try (Gits gits = this.openGit(id)) {
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
    public Gits openGit(Long id) {
        // 查询数据
        ApplicationVcsDO vcs = applicationVcsDAO.selectById(id);
        Valid.notNull(vcs, MessageConst.UNKNOWN_DATA);
        Valid.isTrue(VcsStatus.OK.getStatus().equals(vcs.getVcsStatus()), MessageConst.VCS_UNINITIALIZED);
        // 获取仓库位置
        File vcsPath = new File(Files1.getPath(MachineEnvAttr.VCS_PATH.getValue(), id + Strings.EMPTY));
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
            if (vcs.getVcsPassword() != null) {
                gits.auth(vcs.getVscUsername(), ValueMix.decrypt(vcs.getVcsPassword()));
            }
            return gits;
        } catch (Exception e) {
            throw Exceptions.runtime(MessageConst.VCS_UNABLE_CONNECT, e);
        }
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
