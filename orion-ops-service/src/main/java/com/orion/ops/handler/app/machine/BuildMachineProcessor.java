package com.orion.ops.handler.app.machine;

import com.alibaba.fastjson.JSON;
import com.orion.lang.able.Executable;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.Threads;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.io.Streams;
import com.orion.lang.utils.io.compress.CompressTypeEnum;
import com.orion.lang.utils.io.compress.FileCompressor;
import com.orion.lang.utils.time.Dates;
import com.orion.net.remote.channel.SessionStore;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.StainCode;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.consts.app.ApplicationEnvAttr;
import com.orion.ops.consts.app.BuildStatus;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.consts.message.MessageType;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.ApplicationBuildDAO;
import com.orion.ops.entity.domain.ApplicationActionLogDO;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.ops.handler.app.action.IActionHandler;
import com.orion.ops.handler.app.action.MachineActionStore;
import com.orion.ops.handler.app.build.BuildSessionHolder;
import com.orion.ops.service.api.ApplicationActionLogService;
import com.orion.ops.service.api.ApplicationEnvService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.service.api.WebSideMessageService;
import com.orion.ops.utils.Utils;
import com.orion.spring.SpringHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 构建机器执行器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/12 14:44
 */
@Slf4j
public class BuildMachineProcessor extends AbstractMachineProcessor implements Executable {

    private static ApplicationBuildDAO applicationBuildDAO = SpringHolder.getBean(ApplicationBuildDAO.class);

    private static MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

    private static ApplicationActionLogService applicationActionLogService = SpringHolder.getBean(ApplicationActionLogService.class);

    private static ApplicationEnvService applicationEnvService = SpringHolder.getBean(ApplicationEnvService.class);

    private static BuildSessionHolder buildSessionHolder = SpringHolder.getBean(BuildSessionHolder.class);

    private static WebSideMessageService webSideMessageService = SpringHolder.getBean(WebSideMessageService.class);

    private ApplicationBuildDO record;

    private MachineActionStore store;

    public BuildMachineProcessor(Long id) {
        super(id);
        this.store = new MachineActionStore();
    }

    @Override
    public void exec() {
        log.info("应用构建任务执行提交 buildId: {}", id);
        Threads.start(this, SchedulerPools.APP_BUILD_SCHEDULER);
    }

    @Override
    public void run() {
        log.info("应用构建任务执行开始 buildId: {}", id);
        // 初始化数据
        this.initData();
        // 执行
        super.run();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 查询build
        this.record = applicationBuildDAO.selectById(id);
        log.info("应用构建任务-获取数据-build buildId: {}, record: {}", id, JSON.toJSONString(record));
        // 检查状态
        if (record == null || !BuildStatus.WAIT.getStatus().equals(record.getBuildStatus())) {
            return;
        }
        // 查询action
        List<ApplicationActionLogDO> actions = applicationActionLogService.selectActionByRelId(id, StageType.BUILD);
        actions.forEach(s -> store.getActions().put(s.getId(), s));
        log.info("应用构建任务-获取数据-action buildId: {}, actions: {}", id, JSON.toJSONString(actions));
        // 插入store
        Long vcsId = record.getVcsId();
        store.setRelId(id);
        store.setVcsId(vcsId);
        store.setBranchName(record.getBranchName());
        store.setCommitId(record.getCommitId());
        if (vcsId != null) {
            String vcsClonePath = Files1.getPath(SystemEnvAttr.VCS_PATH.getValue(), vcsId + "/" + record.getId());
            store.setVcsClonePath(vcsClonePath);
        }
        // 创建handler
        this.handlerList = IActionHandler.createHandler(actions, store);
    }

    @Override
    protected boolean checkCanRunnable() {
        return record != null && BuildStatus.WAIT.getStatus().equals(record.getBuildStatus());
    }

    @Override
    protected void completeCallback(Exception e) {
        super.completeCallback(e);
        if (e == null && !terminated) {
            // 复制产物文件
            this.copyBundleFile();
        }
    }

    @Override
    protected void successCallback() {
        // 完成回调
        super.successCallback();
        // 发送站内信
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.ID, record.getId());
        params.put(EventKeys.SEQ, record.getBuildSeq());
        params.put(EventKeys.PROFILE_NAME, record.getProfileName());
        params.put(EventKeys.APP_NAME, record.getAppName());
        params.put(EventKeys.BUILD_SEQ, record.getBuildSeq());
        webSideMessageService.addMessage(MessageType.BUILD_SUCCESS, record.getCreateUserId(), record.getCreateUserName(), params);
    }

    @Override
    protected void exceptionCallback(boolean isMainError, Exception ex) {
        super.exceptionCallback(isMainError, ex);
        // 发送站内信
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.ID, record.getId());
        params.put(EventKeys.SEQ, record.getBuildSeq());
        params.put(EventKeys.APP_NAME, record.getAppName());
        params.put(EventKeys.PROFILE_NAME, record.getProfileName());
        params.put(EventKeys.BUILD_SEQ, record.getBuildSeq());
        webSideMessageService.addMessage(MessageType.BUILD_FAILURE, record.getCreateUserId(), record.getCreateUserName(), params);
    }

    @Override
    protected void openLogger() {
        super.openLogger();
        store.setSuperLogStream(this.logStream);
    }

    /**
     * 复制产物文件
     */
    @SneakyThrows
    private void copyBundleFile() {
        // 查询应用产物目录
        String bundlePath = applicationEnvService.getAppEnvValue(record.getAppId(), record.getProfileId(), ApplicationEnvAttr.BUNDLE_PATH.getKey());
        if (!bundlePath.startsWith(Const.SLASH) && !Files1.isWindowsPath(bundlePath) && store.getVcsClonePath() != null) {
            // 基于代码目录的相对路径
            bundlePath = Files1.getPath(store.getVcsClonePath(), bundlePath);
        }
        // 检查产物文件是否存在
        File bundleFile = new File(bundlePath);
        if (!bundleFile.exists()) {
            throw Exceptions.log("***** 构建产物不存在    " + bundlePath);
        }
        // 复制到dist目录下
        String copyBundlePath = Files1.getPath(SystemEnvAttr.DIST_PATH.getValue(), record.getBundlePath());
        StringBuilder copyLog = new StringBuilder(Const.LF_3)
                .append(StainCode.prefix(StainCode.GLOSS_GREEN))
                .append("***** 已生成产物文件    ")
                .append(StainCode.SUFFIX)
                .append(Utils.getStainKeyWords(copyBundlePath, StainCode.GLOSS_BLUE))
                .append(Const.LF);
        this.appendLog(copyLog.toString());
        if (bundleFile.isFile()) {
            Files1.copy(bundleFile, new File(copyBundlePath));
        } else {
            // 复制文件夹
            Files1.copyDir(bundleFile, new File(copyBundlePath), false);
            // 文件夹打包
            String compressFile = copyBundlePath + "." + Const.SUFFIX_ZIP;
            FileCompressor compressor = CompressTypeEnum.ZIP.compressor().get();
            compressor.addFile(bundleFile);
            compressor.setAbsoluteCompressPath(compressFile);
            compressor.compress();
            StringBuilder compressLog = new StringBuilder()
                    .append(StainCode.prefix(StainCode.GLOSS_GREEN))
                    .append("***** 已生成产物文件zip ")
                    .append(StainCode.SUFFIX)
                    .append(Utils.getStainKeyWords(compressFile, StainCode.GLOSS_BLUE))
                    .append(Const.LF);
            this.appendLog(compressLog.toString());
        }
    }

    @Override
    protected String getLogPath() {
        return record.getLogPath();
    }

    @Override
    protected void openMachineSession() {
        boolean hasCommand = store.getActions().values().stream()
                .map(ApplicationActionLogDO::getActionType)
                .anyMatch(ActionType.BUILD_COMMAND.getType()::equals);
        if (!hasCommand) {
            return;
        }
        // 打开session
        SessionStore sessionStore = machineInfoService.openSessionStore(Const.HOST_MACHINE_ID);
        store.setMachineId(Const.HOST_MACHINE_ID);
        store.setSessionStore(sessionStore);
    }

    @Override
    protected void updateStatus(MachineProcessorStatus status) {
        Date now = new Date();
        ApplicationBuildDO update = new ApplicationBuildDO();
        update.setId(id);
        update.setBuildStatus(BuildStatus.valueOf(status.name()).getStatus());
        update.setUpdateTime(now);
        switch (status) {
            case RUNNABLE:
                this.startTime = now;
                update.setBuildStartTime(now);
                // 添加session
                buildSessionHolder.addSession(id, this);
                break;
            case FINISH:
            case FAILURE:
            case TERMINATED:
                this.endTime = now;
                update.setBuildEndTime(now);
                break;
            default:
                break;
        }
        // 更新
        applicationBuildDAO.updateById(update);
    }

    @Override
    protected void appendStartedLog() {
        StringBuilder log = new StringBuilder()
                .append(Utils.getStainKeyWords("# 开始执行主机构建任务 ", StainCode.GLOSS_GREEN))
                .append(StainCode.prefix(StainCode.GLOSS_BLUE))
                .append("#").append(record.getBuildSeq())
                .append(StainCode.SUFFIX)
                .append(Const.LF);
        log.append("构建应用: ")
                .append(Utils.getStainKeyWords(record.getAppName(), StainCode.GLOSS_BLUE))
                .append(Const.LF);
        log.append("构建环境: ")
                .append(Utils.getStainKeyWords(record.getProfileName(), StainCode.GLOSS_BLUE))
                .append(Const.LF);
        log.append("执行用户: ")
                .append(Utils.getStainKeyWords(record.getCreateUserName(), StainCode.GLOSS_BLUE))
                .append(Const.LF);
        log.append("开始时间: ")
                .append(Utils.getStainKeyWords(Dates.format(startTime), StainCode.GLOSS_BLUE))
                .append(Const.LF);
        if (!Strings.isBlank(record.getDescription())) {
            log.append("构建描述: ")
                    .append(Utils.getStainKeyWords(record.getDescription(), StainCode.GLOSS_BLUE))
                    .append(Const.LF);
        }
        if (!Strings.isBlank(record.getBranchName())) {
            log.append("branch:   ")
                    .append(Utils.getStainKeyWords(record.getBranchName(), StainCode.GLOSS_BLUE))
                    .append(Const.LF);
            log.append("commit:   ")
                    .append(Utils.getStainKeyWords(record.getCommitId(), StainCode.GLOSS_BLUE))
                    .append(Const.LF);
        }
        this.appendLog(log.toString());
    }

    @Override
    public void close() {
        // 释放资源
        super.close();
        // 释放连接
        Streams.close(store.getSessionStore());
        // 移除session
        buildSessionHolder.removeSession(id);
    }

}
