package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.ObjectIds;
import com.orion.lang.collect.LimitList;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.machine.MountKeyStatus;
import com.orion.ops.dao.MachineSecretKeyDAO;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.request.MachineKeyRequest;
import com.orion.ops.entity.vo.MachineSecretKeyVO;
import com.orion.ops.service.api.MachineKeyService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Valid;
import com.orion.ops.utils.ValueMix;
import com.orion.remote.channel.SessionHolder;
import com.orion.utils.Strings;
import com.orion.utils.codec.Base64s;
import com.orion.utils.io.FileWriters;
import com.orion.utils.io.Files1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 机器秘钥服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/5 11:20
 */
@Slf4j
@Service("machineKeyService")
public class MachineKeyServiceImpl implements MachineKeyService {

    @Resource
    private MachineSecretKeyDAO machineSecretKeyDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSecretKey(MachineKeyRequest request) {
        MachineSecretKeyDO key = new MachineSecretKeyDO();
        key.setKeyName(request.getName());
        key.setDescription(request.getDescription());
        String file = PathBuilders.getSecretKeyPath();
        String path = MachineKeyService.getKeyPath(file);
        key.setSecretKeyPath(file);
        Files1.touch(path);
        byte[] keyFileData = Base64s.decode(Strings.bytes(request.getFile()));
        FileWriters.writeFast(path, keyFileData);
        key.setPassword(ValueMix.encrypt(request.getPassword()));
        // 加载key
        SessionHolder.addIdentity(path, request.getPassword());
        machineSecretKeyDAO.insert(key);
        return key.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateSecretKey(MachineKeyRequest request) {
        MachineSecretKeyDO updateKey = new MachineSecretKeyDO();
        Long id = request.getId();
        updateKey.setId(id);
        updateKey.setKeyName(request.getName());
        updateKey.setDescription(request.getDescription());
        updateKey.setUpdateTime(new Date());
        String password = request.getPassword();
        String fileBase64 = request.getFile();
        if (Strings.isBlank(fileBase64) && Strings.isBlank(password)) {
            return machineSecretKeyDAO.updateById(updateKey);
        }
        // 移除原先的key
        MachineSecretKeyDO beforeKey = machineSecretKeyDAO.selectById(id);
        String beforeKeyPath = MachineKeyService.getKeyPath(beforeKey.getSecretKeyPath());
        SessionHolder.removeIdentity(beforeKeyPath);

        if (!Strings.isBlank(fileBase64)) {
            // 修改秘钥文件 将新秘钥保存到本地
            Files1.delete(beforeKeyPath);
            String afterKeyFile = PathBuilders.getSecretKeyPath();
            String afterKeyPath = MachineKeyService.getKeyPath(afterKeyFile);
            Files1.touch(afterKeyPath);
            byte[] keyFileData = Base64s.decode(Strings.bytes(fileBase64));
            FileWriters.writeFast(afterKeyPath, keyFileData);
            updateKey.setSecretKeyPath(afterKeyFile);
            updateKey.setPassword(ValueMix.encrypt(password));
            SessionHolder.addIdentity(afterKeyPath, password);
        } else {
            // 修改密码
            updateKey.setPassword(ValueMix.encrypt(password));
            SessionHolder.addIdentity(beforeKeyPath, password);
        }
        return machineSecretKeyDAO.updateById(updateKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer removeSecretKey(List<Long> idList) {
        int effect = 0;
        for (Long id : idList) {
            MachineSecretKeyDO key = machineSecretKeyDAO.selectById(id);
            if (key == null) {
                continue;
            }
            String secretKeyPath = MachineKeyService.getKeyPath(key.getSecretKeyPath());
            // 移除key
            SessionHolder.removeIdentity(secretKeyPath);
            // 删除key
            Files1.delete(secretKeyPath);
            effect += machineSecretKeyDAO.deleteById(id);
        }
        return effect;
    }

    @Override
    public MachineSecretKeyDO getKeyById(Long id) {
        return machineSecretKeyDAO.selectById(id);
    }

    @Override
    public DataGrid<MachineSecretKeyVO> listKeys(MachineKeyRequest request) {
        final int page = request.getPage();
        final int limit = request.getLimit();
        final boolean checkStatus = request.getMountStatus() != null;
        if (checkStatus) {
            request.setPage(Const.N_1);
            request.setLimit(Const.N_100000);
        }
        LambdaQueryWrapper<MachineSecretKeyDO> wrapper = new LambdaQueryWrapper<MachineSecretKeyDO>()
                .like(Strings.isNotBlank(request.getName()), MachineSecretKeyDO::getKeyName, request.getName())
                .like(Strings.isNotBlank(request.getDescription()), MachineSecretKeyDO::getDescription, request.getDescription())
                .orderByDesc(MachineSecretKeyDO::getCreateTime);
        DataGrid<MachineSecretKeyVO> dataGrid = DataQuery.of(machineSecretKeyDAO)
                .wrapper(wrapper)
                .page(request)
                .dataGrid(MachineSecretKeyVO.class);
        if (!dataGrid.isEmpty()) {
            List<String> loadKeys = SessionHolder.getLoadKeys();
            for (MachineSecretKeyVO row : dataGrid.getRows()) {
                String path = row.getPath();
                File file = new File(MachineKeyService.getKeyPath(path));
                boolean isFile = Files1.isFile(file);
                if (isFile) {
                    boolean match = loadKeys.stream().anyMatch(key -> key.endsWith(path));
                    if (match) {
                        row.setMountStatus(MountKeyStatus.MOUNTED.getStatus());
                    } else {
                        row.setMountStatus(MountKeyStatus.DUMPED.getStatus());
                    }
                } else {
                    row.setMountStatus(MountKeyStatus.NOT_FOUND.getStatus());
                }
            }
        }
        if (!checkStatus) {
            return dataGrid;
        } else {
            // 手动过滤
            List<MachineSecretKeyVO> totalRows = dataGrid.stream().filter(row -> request.getMountStatus().equals(row.getMountStatus()))
                    .collect(Collectors.toList());
            List<MachineSecretKeyVO> rows = new LimitList<>(totalRows, limit).page(page);
            // 封装返回
            DataGrid<MachineSecretKeyVO> newDataGrid = DataGrid.of(rows, totalRows.size());
            newDataGrid.setPage(page);
            newDataGrid.setLimit(limit);
            return newDataGrid;
        }
    }

    @Override
    public Integer mountKey(Long id) {
        MachineSecretKeyDO key = Valid.notNull(machineSecretKeyDAO.selectById(id), "秘钥未找到");
        return this.mountOrDump(key, true);
    }

    @Override
    public Integer dumpKey(Long id) {
        MachineSecretKeyDO key = Valid.notNull(machineSecretKeyDAO.selectById(id), "秘钥未找到");
        return this.mountOrDump(key, false);
    }

    @Override
    public void mountAllKey() {
        List<MachineSecretKeyDO> keys = machineSecretKeyDAO.selectList(null);
        for (MachineSecretKeyDO key : keys) {
            String secretKeyPath = MachineKeyService.getKeyPath(key.getSecretKeyPath());
            File secretKey = new File(secretKeyPath);
            if (!Files1.isFile(secretKey)) {
                log.warn("加载ssh秘钥失败 未找到文件 {} {}", key.getKeyName(), secretKeyPath);
                continue;
            }
            log.info("加载ssh秘钥 {} {}", key.getKeyName(), secretKeyPath);
            String password = ValueMix.decrypt(key.getPassword());
            if (password == null) {
                log.warn("加载ssh秘钥失败 密码错误 {} {}", key.getKeyName(), secretKeyPath);
                continue;
            }
            try {
                SessionHolder.addIdentity(secretKeyPath, password);
            } catch (Exception e) {
                log.error("加载ssh秘钥失败 发生异常 {} {} {}", key.getKeyName(), secretKeyPath, e);
            }
        }
    }

    @Override
    public void dumpAllKey() {
        SessionHolder.removeAllIdentity();
    }

    @Override
    public Integer tempMountKey(String fileData, String password) {
        String path = "/temp_" + ObjectIds.next() + "_id_rsa";
        path = MachineKeyService.getKeyPath(path);
        Files1.touchOnDelete(path);
        byte[] keyFileData = Base64s.decode(Strings.bytes(fileData));
        FileWriters.writeFast(path, keyFileData);
        // 加载key
        SessionHolder.addIdentity(path, password);
        // 检查状态
        return this.getMountStatus(path);
    }

    /**
     * 挂载或卸载key
     *
     * @param key   key
     * @param mount true挂载
     * @return status
     */
    private Integer mountOrDump(MachineSecretKeyDO key, boolean mount) {
        String path = key.getSecretKeyPath();
        String keyPath = MachineKeyService.getKeyPath(path);
        File file = new File(keyPath);
        if (!Files1.isFile(file)) {
            return MountKeyStatus.NOT_FOUND.getStatus();
        }
        if (mount) {
            // 加载key
            SessionHolder.addIdentity(keyPath, ValueMix.decrypt(key.getPassword()));
        } else {
            // 卸载
            SessionHolder.removeIdentity(keyPath);
        }
        // 检查状态
        return this.getMountStatus(path);
    }

    /**
     * 获取加载状态
     *
     * @param path path
     * @return status
     */
    private Integer getMountStatus(String path) {
        List<String> loadKeys = SessionHolder.getLoadKeys();
        boolean match = loadKeys.stream().anyMatch(k -> k.endsWith(path));
        if (match) {
            return MountKeyStatus.MOUNTED.getStatus();
        } else {
            return MountKeyStatus.DUMPED.getStatus();
        }
    }

}
