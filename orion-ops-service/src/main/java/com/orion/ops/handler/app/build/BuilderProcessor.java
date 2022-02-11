package com.orion.ops.handler.app.build;

import com.alibaba.fastjson.JSON;
import com.orion.exception.LogException;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.consts.app.ApplicationEnvAttr;
import com.orion.ops.consts.app.BuildStatus;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.entity.domain.ApplicationBuildActionDO;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.ops.handler.app.build.handler.IBuildHandler;
import com.orion.ops.handler.app.store.BuildStore;
import com.orion.ops.handler.tail.ITailHandler;
import com.orion.ops.handler.tail.TailSessionHolder;
import com.orion.ops.service.api.ApplicationBuildService;
import com.orion.ops.service.api.ApplicationEnvService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.remote.channel.SessionStore;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.Threads;
import com.orion.utils.Valid;
import com.orion.utils.collect.Lists;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.io.compress.CompressTypeEnum;
import com.orion.utils.io.compress.FileCompressor;
import com.orion.utils.time.Dates;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * 构建执行器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/5 22:14
 */
@Slf4j
public class BuilderProcessor implements IBuilderProcessor {

    private static ApplicationBuildService applicationBuildService = SpringHolder.getBean(ApplicationBuildService.class);

    private static MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

    private static ApplicationEnvService applicationEnvService = SpringHolder.getBean(ApplicationEnvService.class);

    private static TailSessionHolder tailSessionHolder = SpringHolder.getBean(TailSessionHolder.class);

    private static BuildSessionHolder buildSessionHolder = SpringHolder.getBean(BuildSessionHolder.class);

    @Getter
    private Long buildId;

    private ApplicationBuildDO record;

    /**
     * 处理器
     */
    private List<IBuildHandler> handlerList;

    private OutputStream logStream;

    private BuildStore store;

    /**
     * 是否已终止
     */
    private volatile boolean terminated;

    public BuilderProcessor(Long buildId) {
        this.buildId = buildId;
        this.store = new BuildStore();
    }

    @Override
    public void exec() {
        log.info("提交应用构建任务 buildId: {}", buildId);
        Threads.start(this, SchedulerPools.APP_BUILD_SCHEDULER);
    }

    @Override
    public void run() {
        log.info("应用构建任务执行开始 buildId: {}", buildId);
        // 查询构建信息
        this.getBuildData();
        // 检查状态
        if (!BuildStatus.WAIT.getStatus().equals(record.getBuildStatus())) {
            return;
        }
        Exception ex = null;
        boolean isMainError = false;
        try {
            // 添加session
            buildSessionHolder.addSession(this);
            // 更新状态
            this.updateStatus(BuildStatus.RUNNABLE);
            // 打开日志
            this.openLogger();
            // 打开机器连接
            this.openMachineSession();
            // 执行
            for (IBuildHandler handler : handlerList) {
                if (ex == null && !terminated) {
                    try {
                        // 执行
                        handler.exec();
                    } catch (Exception e) {
                        if (!terminated) {
                            ex = e;
                        }
                    }
                } else {
                    // 跳过
                    handler.skipped();
                }
            }
            // 复制产物文件
            if (ex == null && !terminated) {
                this.copyBundleFile();
            }
        } catch (Exception e) {
            // 异常
            ex = e;
            isMainError = true;
            log.error("应用构建任务执行失败 buildId: {}, e: {}", buildId, e);
        } finally {
            // 移除session
            buildSessionHolder.removeSession(this.buildId);
        }
        // 更新状态
        if (terminated) {
            this.updateStatus(BuildStatus.TERMINATED);
            this.appendFinishedLog(null, false);
        } else {
            this.updateStatus(ex == null ? BuildStatus.FINISH : BuildStatus.FAILURE);
            this.appendFinishedLog(ex, isMainError);
        }
        // 关闭
        Streams.close(this);
    }

    @Override
    public void terminated() {
        // 设置状态为已停止
        this.terminated = true;
        // 结束正在执行的action
        for (IBuildHandler handler : handlerList) {
            if (ActionStatus.RUNNABLE.equals(handler.getStatus())) {
                handler.terminated();
            }
        }
    }

    /**
     * 获取构建数据
     */
    private void getBuildData() {
        // 查询build
        this.record = applicationBuildService.selectById(buildId);
        Valid.notNull(record, MessageConst.UNKNOWN_DATA);
        log.info("应用构建器-获取数据-build buildId: {}, record: {}", buildId, JSON.toJSONString(record));
        // 检查状态
        if (!BuildStatus.WAIT.getStatus().equals(record.getBuildStatus())) {
            return;
        }
        // 查询action
        List<ApplicationBuildActionDO> actions = applicationBuildService.selectActionById(buildId);
        Valid.notEmpty(actions, MessageConst.UNKNOWN_DATA);
        log.info("应用构建器-获取数据-action buildId: {}, actions: {}", buildId, JSON.toJSONString(actions));
        // 插入store
        store.setBuildId(record.getId());
        store.setVcsId(record.getVcsId());
        store.setBranchName(record.getBranchName());
        store.setCommitId(record.getCommitId());
        String vcsClonePath = Files1.getPath(MachineEnvAttr.VCS_PATH.getValue(), record.getVcsId() + "/" + record.getId());
        store.setVcsClonePath(vcsClonePath);
        actions.forEach(action -> store.getActions().put(action.getId(), action));
        // 创建handler
        this.handlerList = IBuildHandler.createHandler(actions, store);
    }

    /**
     * 打开日志
     */
    private void openLogger() {
        String logPath = Files1.getPath(MachineEnvAttr.LOG_PATH.getValue(), record.getLogPath());
        log.info("应用构建器-打开日志 buildId: {}, path: {}", buildId, logPath);
        File logFile = new File(logPath);
        Files1.touch(logFile);
        this.logStream = Files1.openOutputStreamFastSafe(logFile);
        store.setMainLogStream(logStream);
        store.setMainLogPath(logFile.getAbsolutePath());
        // 拼接开始日志
        this.appendStartedLog();
    }

    /**
     * 打开session
     */
    private void openMachineSession() {
        boolean hasCommand = store.getActions().values().stream()
                .map(ApplicationBuildActionDO::getActionType)
                .anyMatch(ActionType.BUILD_COMMAND.getType()::equals);
        if (!hasCommand) {
            return;
        }
        // 打开session
        SessionStore sessionStore = machineInfoService.openSessionStore(Const.HOST_MACHINE_ID);
        store.setSessionStore(sessionStore);
    }

    /**
     * 更新状态
     *
     * @param status status
     */
    private void updateStatus(BuildStatus status) {
        ApplicationBuildDO update = new ApplicationBuildDO();
        update.setId(buildId);
        update.setBuildStatus(status.getStatus());
        record.setBuildStatus(status.getStatus());
        switch (status) {
            case RUNNABLE:
                update.setBuildStartTime(new Date());
                record.setBuildStartTime(update.getBuildStartTime());
                break;
            case FINISH:
            case FAILURE:
            case TERMINATED:
                update.setBuildEndTime(new Date());
                record.setBuildEndTime(update.getBuildEndTime());
                break;
            default:
                break;
        }
        // 更新
        applicationBuildService.updateById(update);
    }

    /**
     * 复制产物文件
     */
    @SneakyThrows
    private void copyBundleFile() {
        // 查询应用产物目录
        String bundlePath = applicationEnvService.getAppEnvValue(record.getAppId(), record.getProfileId(), ApplicationEnvAttr.BUNDLE_PATH.getKey());
        if (!bundlePath.startsWith(Const.SLASH)) {
            // 基于代码目录的相对路径
            bundlePath = Files1.getPath(store.getVcsClonePath(), bundlePath);
        }
        // 检查产物文件是否存在
        File bundleFile = new File(bundlePath);
        if (!bundleFile.exists()) {
            throw Exceptions.log("***** 构建产物文件不存在: " + bundlePath);
        }
        // 复制
        String copyBundlePath = Files1.getPath(MachineEnvAttr.DIST_PATH.getValue(), record.getBundlePath());
        this.appendLog(Strings.format("\n***** 已生成产物文件 {}\n", copyBundlePath));
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
            this.appendLog(Strings.format("***** 已生成压缩产物文件 {}\n", compressFile));
        }
    }

    /**
     * 拼接开始日志
     */
    private void appendStartedLog() {
        StringBuilder log = new StringBuilder()
                .append("# 开始构建 #").append(record.getBuildSeq()).append(Const.LF)
                .append("# 构建应用: ").append(record.getAppName()).append(Const.TAB)
                .append(record.getAppTag()).append(Const.LF)
                .append("# 构建环境: ").append(record.getProfileName()).append(Const.TAB)
                .append(record.getProfileTag()).append(Const.LF)
                .append("# 执行用户: ").append(record.getCreateUserName()).append(Const.LF)
                .append("# 开始时间: ").append(Dates.format(record.getBuildStartTime())).append(Const.LF);
        if (!Strings.isBlank(record.getDescription())) {
            log.append("# 执行描述: ").append(record.getDescription()).append(Const.LF);
        }
        if (!Strings.isBlank(record.getBranchName())) {
            log.append("# branch: ").append(record.getBranchName()).append(Const.TAB)
                    .append("commitId: ").append(record.getCommitId()).append(Const.LF);
        }
        this.appendLog(log.toString());
    }

    /**
     * 拼接完成日志
     *
     * @param ex          ex
     * @param isMainError isMainError
     */
    private void appendFinishedLog(Exception ex, boolean isMainError) {
        StringBuilder log = new StringBuilder();
        if (ex != null) {
            // 有异常
            log.append("# 构建失败 结束时间: ").append(Dates.format(record.getBuildEndTime()))
                    .append("; used: ").append(record.getBuildEndTime().getTime() - record.getBuildStartTime().getTime())
                    .append("ms\n");
        } else {
            // 无异常
            if (terminated) {
                log.append("# 构建手动停止 结束时间: ").append(Dates.format(record.getBuildEndTime())).append(Const.LF);
            } else {
                log.append("# 构建完成 结束时间: ").append(Dates.format(record.getBuildEndTime()))
                        .append("; used: ").append(record.getBuildEndTime().getTime() - record.getBuildStartTime().getTime())
                        .append("ms\n");
            }
        }
        // 拼接日志
        this.appendLog(log.toString());
        // 拼接异常
        if (ex != null && isMainError) {
            if (ex instanceof LogException) {
                this.appendLog(ex.getMessage() + Const.LF);
            } else {
                this.appendLog(Exceptions.getStackTraceAsString(ex) + Const.LF);
            }
        }
    }

    /**
     * 拼接日志
     *
     * @param log log
     */
    @SneakyThrows
    private void appendLog(String log) {
        logStream.write(log.getBytes());
    }

    @Override
    public void close() {
        // 关闭日志流
        Streams.close(logStream);
        // 关闭handler
        if (!Lists.isEmpty(handlerList)) {
            for (IBuildHandler handler : handlerList) {
                Streams.close(handler);
            }
        }
        // 关闭session
        Streams.close(store.getSessionStore());
        // 异步关闭正在tail的日志
        Threads.start(() -> {
            try {
                Threads.sleep(Const.MS_S_10);
                tailSessionHolder.getSession(Const.HOST_MACHINE_ID, store.getMainLogPath()).forEach(ITailHandler::close);
            } catch (Exception e) {
                log.error("BuilderProcessor-关闭tail失败 {} {}", buildId, e);
                e.printStackTrace();
            }
        });

    }

}
