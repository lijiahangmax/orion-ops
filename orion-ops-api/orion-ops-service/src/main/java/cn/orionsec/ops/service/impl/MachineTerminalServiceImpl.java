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
import cn.orionsec.ops.constant.KeyConst;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.constant.terminal.TerminalConst;
import cn.orionsec.ops.dao.MachineTerminalDAO;
import cn.orionsec.ops.dao.MachineTerminalLogDAO;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.domain.MachineTerminalDO;
import cn.orionsec.ops.entity.domain.MachineTerminalLogDO;
import cn.orionsec.ops.entity.request.machine.MachineTerminalLogRequest;
import cn.orionsec.ops.entity.request.machine.MachineTerminalRequest;
import cn.orionsec.ops.entity.vo.machine.MachineTerminalLogVO;
import cn.orionsec.ops.entity.vo.machine.MachineTerminalVO;
import cn.orionsec.ops.entity.vo.machine.TerminalAccessVO;
import cn.orionsec.ops.service.api.MachineInfoService;
import cn.orionsec.ops.service.api.MachineTerminalService;
import cn.orionsec.ops.utils.Currents;
import cn.orionsec.ops.utils.DataQuery;
import cn.orionsec.ops.utils.EventParamsHolder;
import cn.orionsec.ops.utils.Valid;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.id.UUIds;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.convert.Converts;
import com.orion.lang.utils.io.Files1;
import com.orion.net.remote.TerminalType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 终端service
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/3/31 17:20
 */
@Slf4j
@Service("machineTerminalService")
public class MachineTerminalServiceImpl implements MachineTerminalService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private MachineTerminalDAO machineTerminalDAO;

    @Resource
    private MachineTerminalLogDAO machineTerminalLogDAO;

    @Resource
    private MachineInfoService machineInfoService;

    @Override
    public TerminalAccessVO getAccessConfig(Long machineId) {
        // 获取机器信息
        MachineInfoDO machine = machineInfoService.selectById(machineId);
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        if (!Const.ENABLE.equals(machine.getMachineStatus())) {
            throw Exceptions.disabled(MessageConst.MACHINE_DISABLE);
        }
        // 设置accessToken
        Long userId = Currents.getUserId();
        String token = UUIds.random32();
        // 获取终端配置
        MachineTerminalVO config = this.getMachineConfig(machineId);
        // 设置数据
        TerminalAccessVO access = new TerminalAccessVO();
        access.setId(config.getId());
        access.setAccessToken(token);
        access.setHost(machine.getMachineHost());
        access.setPort(machine.getSshPort());
        access.setMachineName(machine.getMachineName());
        access.setMachineId(machineId);
        access.setUsername(machine.getUsername());
        access.setTerminalType(config.getTerminalType());
        access.setBackgroundColor(config.getBackgroundColor());
        access.setFontSize(config.getFontSize());
        access.setFontFamily(config.getFontFamily());
        access.setFontColor(config.getFontColor());
        access.setEnableWebLink(config.getEnableWebLink());
        // 设置缓存
        String cacheKey = Strings.format(KeyConst.TERMINAL_ACCESS_TOKEN, token);
        redisTemplate.opsForValue().set(cacheKey, userId + "_" + machineId,
                KeyConst.TERMINAL_ACCESS_TOKEN_EXPIRE, TimeUnit.SECONDS);
        log.info("用户获取terminal uid: {} machineId: {} token: {}", userId, machineId, token);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.MACHINE_NAME, machine.getMachineName());
        return access;
    }

    @Override
    public MachineTerminalVO getMachineConfig(Long machineId) {
        LambdaQueryWrapper<MachineTerminalDO> wrapper = new LambdaQueryWrapper<MachineTerminalDO>()
                .eq(MachineTerminalDO::getMachineId, machineId);
        MachineTerminalDO config = machineTerminalDAO.selectOne(wrapper);
        if (config == null) {
            // 初始化
            MachineTerminalDO insert = new MachineTerminalDO();
            insert.setMachineId(machineId);
            insert.setTerminalType(TerminalType.XTERM.getType());
            insert.setBackgroundColor(TerminalConst.BACKGROUND_COLOR);
            insert.setFontColor(TerminalConst.FONT_COLOR);
            insert.setFontSize(TerminalConst.FONT_SIZE);
            insert.setFontFamily(TerminalConst.FONT_FAMILY);
            insert.setEnableWebLink(Const.DISABLE);
            machineTerminalDAO.insert(insert);
            config = insert;
        }
        return Converts.to(config, MachineTerminalVO.class);
    }

    @Override
    public Integer updateSetting(MachineTerminalRequest request) {
        // 查询配置
        Long id = request.getId();
        MachineTerminalDO beforeConfig = machineTerminalDAO.selectById(id);
        Valid.notNull(beforeConfig, MessageConst.UNKNOWN_DATA);
        // 查询机器信息
        MachineInfoDO machineInfo = machineInfoService.selectById(beforeConfig.getMachineId());
        Valid.notNull(machineInfo, MessageConst.UNKNOWN_DATA);
        // 设置修改信息
        MachineTerminalDO update = new MachineTerminalDO();
        update.setId(id);
        update.setTerminalType(request.getTerminalType());
        update.setFontSize(request.getFontSize());
        update.setFontFamily(request.getFontFamily());
        update.setFontColor(request.getFontColor());
        update.setBackgroundColor(request.getBackgroundColor());
        update.setUpdateTime(new Date());
        update.setEnableWebLink(request.getEnableWebLink());
        // 修改
        int effect = machineTerminalDAO.updateById(update);
        // 设置日志参数
        EventParamsHolder.addParams(request);
        EventParamsHolder.addParam(EventKeys.NAME, machineInfo.getMachineName());
        return effect;
    }

    @Override
    public Long addTerminalLog(MachineTerminalLogDO entity) {
        machineTerminalLogDAO.insert(entity);
        return entity.getId();
    }

    @Override
    public Integer updateAccessLog(String token, MachineTerminalLogDO entity) {
        LambdaQueryWrapper<MachineTerminalLogDO> wrapper = new LambdaQueryWrapper<MachineTerminalLogDO>()
                .eq(MachineTerminalLogDO::getAccessToken, token);
        return machineTerminalLogDAO.update(entity, wrapper);
    }

    @Override
    public DataGrid<MachineTerminalLogVO> listAccessLog(MachineTerminalLogRequest request) {
        if (!Currents.isAdministrator()) {
            request.setUserId(Currents.getUserId());
        }
        LambdaQueryWrapper<MachineTerminalLogDO> wrapper = new LambdaQueryWrapper<MachineTerminalLogDO>()
                .like(Strings.isNotBlank(request.getAccessToken()), MachineTerminalLogDO::getAccessToken, request.getAccessToken())
                .like(Strings.isNotBlank(request.getMachineHost()), MachineTerminalLogDO::getMachineHost, request.getMachineHost())
                .like(Strings.isNotBlank(request.getMachineName()), MachineTerminalLogDO::getMachineName, request.getMachineName())
                .like(Strings.isNotBlank(request.getUsername()), MachineTerminalLogDO::getUsername, request.getUsername())
                .like(Objects.nonNull(request.getUserId()), MachineTerminalLogDO::getUserId, request.getUserId())
                .eq(Objects.nonNull(request.getMachineId()), MachineTerminalLogDO::getMachineId, request.getMachineId())
                .eq(Objects.nonNull(request.getCloseCode()), MachineTerminalLogDO::getCloseCode, request.getCloseCode())
                .between(Objects.nonNull(request.getConnectedTimeStart()) && Objects.nonNull(request.getConnectedTimeEnd()),
                        MachineTerminalLogDO::getConnectedTime, request.getConnectedTimeStart(), request.getConnectedTimeEnd())
                .between(Objects.nonNull(request.getDisconnectedTimeStart()) && Objects.nonNull(request.getDisconnectedTimeEnd()),
                        MachineTerminalLogDO::getDisconnectedTime, request.getDisconnectedTimeStart(), request.getDisconnectedTimeEnd())
                .orderByDesc(MachineTerminalLogDO::getCreateTime);
        return DataQuery.of(machineTerminalLogDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(MachineTerminalLogVO.class);
    }

    @Override
    public Integer deleteTerminalLog(List<Long> idList) {
        // 删除
        int effect = machineTerminalLogDAO.deleteBatchIds(idList);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID_LIST, idList);
        EventParamsHolder.addParam(EventKeys.COUNT, idList.size());
        return effect;
    }

    @Override
    public Integer deleteTerminalByMachineIdList(List<Long> machineIdList) {
        LambdaQueryWrapper<MachineTerminalLogDO> wrapper = new LambdaQueryWrapper<MachineTerminalLogDO>()
                .in(MachineTerminalLogDO::getMachineId, machineIdList);
        return machineTerminalLogDAO.delete(wrapper);
    }

    @Override
    public String getTerminalScreenFilePath(Long id) {
        LambdaQueryWrapper<MachineTerminalLogDO> wrapper = new LambdaQueryWrapper<MachineTerminalLogDO>()
                .eq(!Currents.isAdministrator(), MachineTerminalLogDO::getUserId, Currents.getUserId())
                .eq(MachineTerminalLogDO::getId, id);
        return Optional.ofNullable(machineTerminalLogDAO.selectOne(wrapper))
                .map(MachineTerminalLogDO::getScreenPath)
                .filter(Strings::isNotBlank)
                .map(s -> Files1.getPath(SystemEnvAttr.SCREEN_PATH.getValue(), s))
                .orElse(null);
    }

}
