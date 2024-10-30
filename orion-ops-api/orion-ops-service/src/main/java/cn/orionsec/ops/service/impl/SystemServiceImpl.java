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
package cn.orionsec.ops.service.impl;

import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.common.EnableType;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.system.SystemCleanType;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.constant.system.ThreadPoolMetricsType;
import cn.orionsec.ops.entity.domain.SystemEnvDO;
import cn.orionsec.ops.entity.dto.system.SystemSpaceAnalysisDTO;
import cn.orionsec.ops.entity.request.system.ConfigIpListRequest;
import cn.orionsec.ops.entity.request.system.SystemEnvRequest;
import cn.orionsec.ops.entity.vo.system.IpListConfigVO;
import cn.orionsec.ops.entity.vo.system.SystemAnalysisVO;
import cn.orionsec.ops.entity.vo.system.SystemOptionVO;
import cn.orionsec.ops.entity.vo.system.ThreadPoolMetricsVO;
import cn.orionsec.ops.interceptor.IpFilterInterceptor;
import cn.orionsec.ops.service.api.SystemEnvService;
import cn.orionsec.ops.service.api.SystemService;
import cn.orionsec.ops.utils.EventParamsHolder;
import cn.orionsec.ops.utils.FileCleaner;
import cn.orionsec.ops.utils.Utils;
import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.Threads;
import com.orion.lang.utils.Valid;
import com.orion.lang.utils.convert.Converts;
import com.orion.lang.utils.io.Files1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 系统服务实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 22:15
 */
@Slf4j
@Service("systemService")
public class SystemServiceImpl implements SystemService {

    private final SystemSpaceAnalysisDTO systemSpace;

    @Resource
    private SystemEnvService systemEnvService;

    @Resource
    private IpFilterInterceptor ipFilterInterceptor;

    public SystemServiceImpl() {
        this.systemSpace = new SystemSpaceAnalysisDTO();
    }

    @Override
    public IpListConfigVO getIpInfo(String ip) {
        IpListConfigVO ipConfig = new IpListConfigVO();
        // 查询黑名单
        ipConfig.setBlackIpList(SystemEnvAttr.BLACK_IP_LIST.getValue());
        // 查询白名单
        ipConfig.setWhiteIpList(SystemEnvAttr.WHITE_IP_LIST.getValue());
        // 查询是否启用过滤
        ipConfig.setEnableIpFilter(EnableType.of(SystemEnvAttr.ENABLE_IP_FILTER.getValue()).getValue());
        // 查询是否启用IP白名单
        ipConfig.setEnableWhiteIpList(EnableType.of(SystemEnvAttr.ENABLE_WHITE_IP_LIST.getValue()).getValue());
        // ip
        ipConfig.setCurrentIp(ip);
        // ip 位置
        ipConfig.setIpLocation(Utils.getIpLocation(ip));
        return ipConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void configIpFilterList(ConfigIpListRequest request) {
        // 检查名单
        String blackIpList = request.getBlackIpList();
        String whiteIpList = request.getWhiteIpList();
        this.validIpConfig(blackIpList);
        this.validIpConfig(whiteIpList);
        // 设置黑名单
        SystemEnvDO blackEnv = systemEnvService.selectByName(SystemEnvAttr.BLACK_IP_LIST.getKey());
        SystemEnvRequest updateBlack = new SystemEnvRequest();
        updateBlack.setValue(blackIpList);
        systemEnvService.updateEnv(blackEnv, updateBlack);
        SystemEnvAttr.BLACK_IP_LIST.setValue(blackIpList);
        // 设置白名单
        SystemEnvDO whiteEnv = systemEnvService.selectByName(SystemEnvAttr.WHITE_IP_LIST.getKey());
        SystemEnvRequest updateWhite = new SystemEnvRequest();
        updateWhite.setValue(whiteIpList);
        systemEnvService.updateEnv(whiteEnv, updateWhite);
        SystemEnvAttr.WHITE_IP_LIST.setValue(whiteIpList);
        // 更改启用状态
        EnableType enableIpFilter = EnableType.of(request.getEnableIpFilter());
        SystemEnvDO filterEnv = systemEnvService.selectByName(SystemEnvAttr.ENABLE_IP_FILTER.getKey());
        SystemEnvRequest updateFilter = new SystemEnvRequest();
        updateFilter.setValue(enableIpFilter.getLabel());
        systemEnvService.updateEnv(filterEnv, updateFilter);
        SystemEnvAttr.ENABLE_IP_FILTER.setValue(enableIpFilter.getLabel());
        // 更改启用列表
        EnableType enableWhiteIp = EnableType.of(request.getEnableWhiteIpList());
        SystemEnvDO enableWhiteIpEnv = systemEnvService.selectByName(SystemEnvAttr.ENABLE_WHITE_IP_LIST.getKey());
        SystemEnvRequest updateEnableWhiteIp = new SystemEnvRequest();
        updateEnableWhiteIp.setValue(enableWhiteIp.getLabel());
        systemEnvService.updateEnv(enableWhiteIpEnv, updateEnableWhiteIp);
        SystemEnvAttr.ENABLE_WHITE_IP_LIST.setValue(enableWhiteIp.getLabel());
        // 设置日志参数
        EventParamsHolder.addParams(request);
        // 设置 ip 过滤器
        Boolean enableIpFilterValue = enableIpFilter.getValue();
        Boolean enableWhiteIpValue = enableWhiteIp.getValue();
        ipFilterInterceptor.set(enableIpFilterValue, enableWhiteIpValue, enableWhiteIpValue ? whiteIpList : blackIpList);
    }

    @Override
    public void cleanSystemFile(SystemCleanType cleanType) {
        // 清理
        Threads.start(() -> FileCleaner.cleanDir(cleanType));
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.LABEL, cleanType.getLabel());
    }

    @Override
    public void analysisSystemSpace() {
        // 临时文件
        File tempPath = new File(SystemEnvAttr.TEMP_PATH.getValue());
        List<File> tempFiles = Files1.listFiles(tempPath, true);
        long tempFilesBytes = tempFiles.stream().mapToLong(File::length).sum();
        systemSpace.setTempFileCount(tempFiles.size());
        systemSpace.setTempFileSize(Files1.getSize(tempFilesBytes));
        tempFiles.clear();

        // 日志文件
        File logPath = new File(SystemEnvAttr.LOG_PATH.getValue());
        List<File> logFiles = Files1.listFiles(logPath, true);
        long logFilesBytes = logFiles.stream().mapToLong(File::length).sum();
        systemSpace.setLogFileCount(logFiles.size());
        systemSpace.setLogFileSize(Files1.getSize(logFilesBytes));
        logFiles.clear();

        // 交换区文件
        File swapPath = new File(SystemEnvAttr.SWAP_PATH.getValue());
        List<File> swapFiles = Files1.listFiles(swapPath, true);
        long swapFilesBytes = swapFiles.stream().mapToLong(File::length).sum();
        systemSpace.setSwapFileCount(swapFiles.size());
        systemSpace.setSwapFileSize(Files1.getSize(swapFilesBytes));
        swapFiles.clear();

        // 构建产物
        File buildPath = new File(SystemEnvAttr.DIST_PATH.getValue() + Const.BUILD_DIR);
        List<File> buildFiles = Files1.listFiles(buildPath, true);
        long buildFilesBytes = buildFiles.stream().filter(File::isFile).mapToLong(File::length).sum();
        int distVersions = Files1.listDirs(buildPath).size();
        systemSpace.setDistVersionCount(distVersions);
        systemSpace.setDistFileSize(Files1.getSize(buildFilesBytes));
        buildFiles.clear();

        // 文件太多会导致 oom
        // // 应用仓库
        // File repoPath = new File(SystemEnvAttr.REPO_PATH.getValue());
        // List<File> repoPaths = Files1.listFilesFilter(repoPath, (f, n) -> f.isDirectory() && !Const.EVENT.equals(n), false, true);
        // int repoVersionCount = 0;
        // long repoDirFilesBytes = 0L;
        // for (File repoDir : repoPaths) {
        //     repoVersionCount += Files1.listDirs(repoDir).size();
        //     List<File> repoDirFiles = Files1.listFiles(repoDir, true);
        //     repoDirFilesBytes += repoDirFiles.stream().mapToLong(File::length).sum();
        // }
        // systemSpace.setRepoVersionCount(repoVersionCount);
        // systemSpace.setRepoVersionFileSize(Files1.getSize(repoDirFilesBytes));
        // repoPaths.clear();

        // 录屏文件
        File screenPath = new File(SystemEnvAttr.SCREEN_PATH.getValue());
        List<File> screenFiles = Files1.listFiles(screenPath, true);
        long screenFilesBytes = screenFiles.stream().mapToLong(File::length).sum();
        systemSpace.setScreenFileCount(screenFiles.size());
        systemSpace.setScreenFileSize(Files1.getSize(screenFilesBytes));
        screenFiles.clear();
        log.info("分析占用磁盘空间完成 {}", JSON.toJSONString(systemSpace));
    }

    @Override
    public SystemAnalysisVO getSystemAnalysis() {
        SystemAnalysisVO vo = Converts.to(systemSpace, SystemAnalysisVO.class);
        // 文件清理
        vo.setFileCleanThreshold(Integer.valueOf(SystemEnvAttr.FILE_CLEAN_THRESHOLD.getValue()));
        vo.setAutoCleanFile(EnableType.of(SystemEnvAttr.ENABLE_AUTO_CLEAN_FILE.getValue()).getValue());
        return vo;
    }

    @Override
    public void updateSystemOption(SystemEnvAttr env, String value) {
        String key = env.getKey();
        String beforeValue = env.getValue();
        // 更新系统配置
        SystemEnvDO systemEnv = systemEnvService.selectByName(key);
        SystemEnvRequest updateEnv = new SystemEnvRequest();
        updateEnv.setValue(value);
        systemEnvService.updateEnv(systemEnv, updateEnv);
        env.setValue(value);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.KEY, env.getDescription());
        EventParamsHolder.addParam(EventKeys.LABEL, env.getDescription());
        EventParamsHolder.addParam(EventKeys.BEFORE, beforeValue);
        EventParamsHolder.addParam(EventKeys.AFTER, value);
    }

    @Override
    public SystemOptionVO getSystemOptions() {
        SystemOptionVO options = new SystemOptionVO();
        options.setAutoCleanFile(EnableType.of(SystemEnvAttr.ENABLE_AUTO_CLEAN_FILE.getValue()).getValue());
        options.setFileCleanThreshold(Integer.valueOf(SystemEnvAttr.FILE_CLEAN_THRESHOLD.getValue()));
        options.setAllowMultipleLogin(EnableType.of(SystemEnvAttr.ALLOW_MULTIPLE_LOGIN.getValue()).getValue());
        options.setLoginFailureLock(EnableType.of(SystemEnvAttr.LOGIN_FAILURE_LOCK.getValue()).getValue());
        options.setLoginIpBind(EnableType.of(SystemEnvAttr.LOGIN_IP_BIND.getValue()).getValue());
        options.setLoginTokenAutoRenew(EnableType.of(SystemEnvAttr.LOGIN_TOKEN_AUTO_RENEW.getValue()).getValue());
        options.setLoginTokenExpire(Integer.valueOf(SystemEnvAttr.LOGIN_TOKEN_EXPIRE.getValue()));
        options.setLoginFailureLockThreshold(Integer.valueOf(SystemEnvAttr.LOGIN_FAILURE_LOCK_THRESHOLD.getValue()));
        options.setLoginTokenAutoRenewThreshold(Integer.valueOf(SystemEnvAttr.LOGIN_TOKEN_AUTO_RENEW_THRESHOLD.getValue()));
        options.setResumeEnableSchedulerTask(EnableType.of(SystemEnvAttr.RESUME_ENABLE_SCHEDULER_TASK.getValue()).getValue());
        options.setTerminalActivePushHeartbeat(EnableType.of(SystemEnvAttr.TERMINAL_ACTIVE_PUSH_HEARTBEAT.getValue()).getValue());
        options.setSftpUploadThreshold(Integer.valueOf(SystemEnvAttr.SFTP_UPLOAD_THRESHOLD.getValue()));
        options.setStatisticsCacheExpire(Integer.valueOf(SystemEnvAttr.STATISTICS_CACHE_EXPIRE.getValue()));
        return options;
    }

    @Override
    public List<ThreadPoolMetricsVO> getThreadPoolMetrics() {
        return Arrays.stream(ThreadPoolMetricsType.values())
                .map(this::getThreadPoolMetrics)
                .collect(Collectors.toList());
    }

    /**
     * 校验 ip 过滤列表
     *
     * @param ipList ipList
     */
    private void validIpConfig(String ipList) {
        if (Strings.isBlank(ipList)) {
            return;
        }
        String[] lines = ipList.split(Const.LF);
        for (String ip : lines) {
            if (Strings.isBlank(ip)) {
                continue;
            }
            Valid.isTrue(Utils.validIpLine(ip), Strings.format("{} " + MessageConst.INVALID_CONFIG, ip));
        }
    }


    /**
     * 获取线程池指标
     *
     * @param metricsType 指标类型
     * @return 指标
     */
    private ThreadPoolMetricsVO getThreadPoolMetrics(ThreadPoolMetricsType metricsType) {
        ThreadPoolMetricsVO metrics = new ThreadPoolMetricsVO();
        metrics.setType(metricsType.getType());
        ThreadPoolExecutor executor = metricsType.getExecutor();
        metrics.setActiveThreadCount(executor.getActiveCount());
        metrics.setTotalTaskCount(executor.getTaskCount());
        metrics.setCompletedTaskCount(executor.getCompletedTaskCount());
        metrics.setQueueSize(executor.getQueue().size());
        return metrics;
    }

}
