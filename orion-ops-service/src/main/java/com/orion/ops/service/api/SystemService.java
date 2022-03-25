package com.orion.ops.service.api;

import com.orion.ops.consts.system.SystemCleanType;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.entity.request.ConfigIpListRequest;
import com.orion.ops.entity.vo.IpListConfigVO;
import com.orion.ops.entity.vo.SystemAnalysisVO;
import com.orion.ops.entity.vo.SystemOptionVO;
import com.orion.ops.entity.vo.ThreadPoolMetricsVO;

import java.util.List;

/**
 * 系统服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 22:15
 */
public interface SystemService {

    /**
     * 获取 ip 配置
     *
     * @param ip ip
     * @return ip 信息
     */
    IpListConfigVO getIpInfo(String ip);

    /**
     * 配置 ip 列表
     *
     * @param request request
     */
    void configIpList(ConfigIpListRequest request);

    /**
     * 清理系统文件
     *
     * @param cleanType 文件类型
     */
    void cleanSystemFile(SystemCleanType cleanType);

    /**
     * 分析磁盘占用空间
     */
    void analysisSystemSpace();

    /**
     * 获取系统分析信息
     *
     * @return 系统分析信息
     */
    SystemAnalysisVO getSystemAnalysis();

    /**
     * 更新系统配置
     *
     * @param env   env
     * @param value value
     */
    void updateSystemOption(SystemEnvAttr env, String value);

    /**
     * 获取系统配置项
     *
     * @return 配置项
     */
    SystemOptionVO getSystemOptions();

    /**
     * 获取线程池指标
     *
     * @return 指标
     */
    List<ThreadPoolMetricsVO> getThreadPoolMetrics();

}
