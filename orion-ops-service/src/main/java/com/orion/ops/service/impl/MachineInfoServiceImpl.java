package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.exception.AuthenticationException;
import com.orion.exception.ConnectionRuntimeException;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.ProxyType;
import com.orion.ops.consts.SyncMachineProperties;
import com.orion.ops.consts.protocol.TerminalConst;
import com.orion.ops.dao.MachineEnvDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.dao.MachineProxyDAO;
import com.orion.ops.entity.domain.MachineEnvDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.domain.MachineProxyDO;
import com.orion.ops.entity.request.MachineInfoRequest;
import com.orion.ops.entity.vo.MachineInfoVO;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.ValueMix;
import com.orion.process.Processes;
import com.orion.remote.channel.SessionHolder;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.ssh.CommandExecutor;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.Valid;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.Streams;
import com.orion.utils.net.IPs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 机器信息服务
 *
 * @author Jiahang Li
 * @since 2021-03-29
 */
@Slf4j
@Service("machineInfoService")
public class MachineInfoServiceImpl implements MachineInfoService {

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Resource
    private MachineProxyDAO machineProxyDAO;

    @Resource
    private MachineEnvDAO machineEnvDAO;

    @Resource
    private MachineEnvService machineEnvService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addUpdateMachine(MachineInfoRequest request) {
        Long id = request.getId();
        // 检查proxyId
        this.checkProxy(request.getProxyId());
        MachineInfoDO entity = new MachineInfoDO();
        String password = request.getPassword();
        this.copyProperties(request, entity);
        if (id == null) {
            // 添加机器
            entity.setMachineStatus(Const.ENABLE);
            machineInfoDAO.insert(entity);
            if (password != null) {
                entity.setPassword(ValueMix.encrypt(password, entity.getId() + Const.ORION_OPS));
                machineInfoDAO.updateById(entity);
            }
            id = entity.getId();
            // 添加环境变量
            machineEnvService.initEnv(id);
            return id;
        } else {
            if (password != null) {
                entity.setPassword(ValueMix.encrypt(password, entity.getId() + Const.ORION_OPS));
            }
            // 修改
            return (long) machineInfoDAO.updateById(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteMachine(List<Long> idList) {
        int effect = 0;
        for (Long id : idList) {
            effect += machineInfoDAO.deleteById(id);
            LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                    .eq(MachineEnvDO::getMachineId, id);
            machineEnvDAO.delete(wrapper);
        }
        return effect;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateStatus(List<Long> idList, Integer status) {
        int effect = 0;
        for (Long id : idList) {
            MachineInfoDO entity = new MachineInfoDO();
            entity.setId(id);
            entity.setMachineStatus(status);
            effect += machineInfoDAO.updateById(entity);
        }
        return effect;
    }

    @Override
    public DataGrid<MachineInfoVO> listMachine(MachineInfoRequest request) {
        LambdaQueryWrapper<MachineInfoDO> wrapper = new LambdaQueryWrapper<MachineInfoDO>()
                .like(Objects.nonNull(request.getHost()), MachineInfoDO::getMachineHost, request.getHost())
                .like(Objects.nonNull(request.getName()), MachineInfoDO::getMachineName, request.getName())
                .like(Objects.nonNull(request.getTag()), MachineInfoDO::getMachineTag, request.getTag())
                .like(Objects.nonNull(request.getDescription()), MachineInfoDO::getDescription, request.getDescription())
                .like(Objects.nonNull(request.getUsername()), MachineInfoDO::getUsername, request.getUsername())
                .eq(Objects.nonNull(request.getProxyId()), MachineInfoDO::getProxyId, request.getProxyId())
                .eq(Objects.nonNull(request.getSystemType()), MachineInfoDO::getSystemType, request.getSystemType())
                .eq(Objects.nonNull(request.getStatus()), MachineInfoDO::getMachineStatus, request.getStatus())
                .eq(Objects.nonNull(request.getId()), MachineInfoDO::getId, request.getId())
                .ne(Objects.nonNull(request.getExcludeId()), MachineInfoDO::getId, request.getExcludeId())
                .ne(Const.ENABLE.equals(request.getSkipHost()), MachineInfoDO::getId, 1)
                .orderByAsc(MachineInfoDO::getId);
        return DataQuery.of(machineInfoDAO)
                .wrapper(wrapper)
                .page(request)
                .dataGrid(MachineInfoVO.class);
    }

    @Override
    public MachineInfoVO machineDetail(Long id) {
        MachineInfoDO machine = machineInfoDAO.selectById(id);
        Valid.notNull(machine, "未查询到机器");
        MachineInfoVO vo = Converts.to(machine, MachineInfoVO.class);
        Optional.ofNullable(machine.getProxyId())
                .map(machineProxyDAO::selectById)
                .ifPresent(p -> {
                    vo.setProxyHost(p.getProxyHost());
                    vo.setProxyPort(p.getProxyPort());
                    vo.setProxyType(p.getProxyType());
                });
        return vo;
    }

    @Override
    public String syncProperties(MachineInfoRequest request) {
        Long id = request.getId();
        String syncProp = request.getSyncProp();
        SyncMachineProperties func = SyncMachineProperties.of(syncProp);
        if (func == null) {
            throw Exceptions.invalidArgument("无法同步属性");
        }
        MachineInfoDO machine = new MachineInfoDO();
        machine.setId(id);
        String res;
        switch (func) {
            case MACHINE_NAME:
                res = this.getCommandResult(id, func.getCommand());
                machine.setMachineName(res);
                break;
            case SYSTEM_VERSION:
                res = this.getCommandResult(id, func.getCommand());
                machine.setSystemVersion(res);
                break;
            default:
                return null;
        }
        if (res == null) {
            return null;
        }
        machineInfoDAO.updateById(machine);
        return res;
    }

    @Override
    public MachineInfoDO selectById(Long id) {
        return machineInfoDAO.selectById(id);
    }

    @Override
    public Integer testPing(Long id) {
        MachineInfoDO machine = machineInfoDAO.selectById(id);
        Valid.notNull(machine, Const.INVALID_MACHINE);
        boolean ping = IPs.ping(machine.getMachineHost(), Const.MS_S_3);
        return ping ? Const.ENABLE : Const.DISABLE;
    }

    @Override
    public Integer testConnect(Long id) {
        SessionStore s = null;
        try {
            s = this.openSessionStore(id);
            return Const.ENABLE;
        } catch (Exception e) {
            return Const.DISABLE;
        } finally {
            Streams.close(s);
        }
    }

    @Override
    public SessionStore openSessionStore(Long id) {
        MachineInfoDO machine = machineInfoDAO.selectById(id);
        Valid.notNull(machine, Const.INVALID_MACHINE);
        Long proxyId = machine.getProxyId();
        SessionStore session;
        try {
            session = SessionHolder.getSession(machine.getMachineHost(), machine.getSshPort(), machine.getUsername());
            if (machine.getPassword() != null) {
                session.setPassword(ValueMix.decrypt(machine.getPassword(), machine.getId() + Const.ORION_OPS));
            }
            MachineProxyDO proxy = null;
            if (proxyId != null) {
                proxy = machineProxyDAO.selectById(proxyId);
            }
            if (proxy != null) {
                ProxyType proxyType = ProxyType.of(proxy.getProxyType());
                String password = proxy.getProxyPassword();
                if (!Strings.isBlank(password)) {
                    password = ValueMix.decrypt(password);
                }
                if (ProxyType.HTTP.equals(proxyType)) {
                    session.setHttpProxy(proxy.getProxyHost(), proxy.getProxyPort(), proxy.getProxyUsername(), password);
                } else if (ProxyType.SOCKET4.equals(proxyType)) {
                    session.setSocket4Proxy(proxy.getProxyHost(), proxy.getProxyPort(), proxy.getProxyUsername(), password);
                } else if (ProxyType.SOCKET5.equals(proxyType)) {
                    session.setSocket5Proxy(proxy.getProxyHost(), proxy.getProxyPort(), proxy.getProxyUsername(), password);
                }
                session.setHttpProxy(proxy.getProxyHost(), proxy.getProxyPort(), proxy.getProxyUsername(), password);
            }
            session.connect(TerminalConst.TERMINAL_CONNECT_TIMEOUT);
            log.info("远程机器建立连接-成功 {}@{}/{}", machine.getUsername(), machine.getMachineHost(), machine.getSshPort());
            return session;
        } catch (Exception e) {
            log.error("远程机器建立连接-失败 {}@{}/{} {}", machine.getUsername(), machine.getMachineHost(), machine.getSshPort(), e);
            throw e;
        }
    }

    @Override
    public String getCommandResult(Long id, String command) {
        String res;
        if (id.equals(1L)) {
            // 本机
            res = this.runHostCommand(command);
        } else {
            // 远程
            res = this.runRemoteCommand(id, command);
        }
        if (res == null) {
            return null;
        }
        if (res.endsWith(Const.LF)) {
            return res.substring(0, res.length() - 1);
        }
        return res;
    }

    /**
     * 执行本地命令
     */
    private String runHostCommand(String command) {
        try {
            return Processes.getOutputResultString(command);
        } catch (Exception e) {
            log.error("执行本机命令-失败 {} {}", command, e);
            return null;
        }
    }

    /**
     * 执行远程机群命令
     */
    private String runRemoteCommand(Long id, String command) {
        SessionStore session = null;
        CommandExecutor executor = null;
        try {
            session = this.openSessionStore(id);
            executor = session.getCommandExecutor(command);
            executor.connect(3000);
            String res = SessionStore.getCommandOutputResultString(executor);
            log.info("执行机器命令-成功 {} {} {}", id, command, res);
            return res;
        } catch (Exception e) {
            log.error("执行机器命令-失败 {} {} {}", id, command, e);
            if (e instanceof ConnectionRuntimeException | e instanceof AuthenticationException) {
                throw e;
            }
            return null;
        } finally {
            Streams.close(executor);
            Streams.close(session);
        }
    }

    /**
     * 检查代理
     */
    private void checkProxy(Long proxyId) {
        if (proxyId == null) {
            return;
        }
        MachineProxyDO proxy = machineProxyDAO.selectById(proxyId);
        Valid.notNull(proxy, "未查询到代理信息");
    }

    /**
     * 复制属性
     */
    private void copyProperties(MachineInfoRequest request, MachineInfoDO entity) {
        entity.setId(request.getId());
        entity.setProxyId(request.getProxyId());
        entity.setMachineHost(request.getHost());
        entity.setSshPort(request.getSshPort());
        entity.setMachineName(request.getName());
        entity.setMachineTag(request.getTag());
        entity.setDescription(request.getDescription());
        entity.setUsername(request.getUsername());
        entity.setPassword(null);
        entity.setAuthType(request.getAuthType());
        entity.setSystemType(request.getSystemType());
        entity.setSystemVersion(request.getSystemVersion());
        entity.setMachineStatus(request.getStatus());
    }

}
