package com.orion.ops.service.impl;

import com.orion.location.region.LocationRegions;
import com.orion.location.region.core.Region;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.EnableType;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.event.EventParamsHolder;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.entity.domain.SystemEnvDO;
import com.orion.ops.entity.request.ConfigIpListRequest;
import com.orion.ops.entity.request.SystemEnvRequest;
import com.orion.ops.entity.vo.IpListConfigVO;
import com.orion.ops.service.api.SystemEnvService;
import com.orion.ops.service.api.SystemService;
import com.orion.ops.utils.Utils;
import com.orion.utils.Strings;
import com.orion.utils.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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

    @Resource
    private SystemEnvService systemEnvService;

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
        Region region = LocationRegions.getRegion(ip);
        if (region != null) {
            StringBuilder location = new StringBuilder()
                    .append(region.getProvince())
                    .append(Const.DASHED)
                    .append(region.getCity())
                    .append(Const.DASHED)
                    .append(region.getCountry());
            location.append(" (").append(region.getNet()).append(')');
            ipConfig.setIpLocation(location.toString());
        }
        return ipConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void configIpList(ConfigIpListRequest request) {
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

}
