package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.ObjectIds;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.MountKeyStatus;
import com.orion.ops.dao.MachineSecretKeyDAO;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.request.MachineKeyRequest;
import com.orion.ops.entity.vo.MachineSecretKeyVO;
import com.orion.ops.service.api.MachineKeyService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.ops.utils.ValueMix;
import com.orion.remote.channel.SessionHolder;
import com.orion.utils.Strings;
import com.orion.utils.codec.Base64s;
import com.orion.utils.io.FileWriters;
import com.orion.utils.io.Files1;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/5 11:20
 */
@Service("machineKeyService")
public class MachineKeyServiceImpl implements MachineKeyService {

    @Resource
    private MachineSecretKeyDAO machineSecretKeyDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSecretKey(MachineKeyRequest request) {
        MachineSecretKeyDO key = new MachineSecretKeyDO();
        key.setKeyName(request.getName());
        key.setPassword(request.getPassword());
        key.setDescription(request.getDescription());
        String file = "/" + ObjectIds.next() + "_id_rsa";
        String path = MachineKeyService.getKeyPath(file);
        key.setSecretKeyPath(file);
        Files1.touch(path);
        byte[] keyFileData = Base64s.decode(Strings.bytes(request.getFile()));
        FileWriters.writeFast(path, keyFileData);
        String password = key.getPassword();
        if (!Strings.isBlank(password)) {
            key.setPassword(ValueMix.encrypt(password));
        }
        // 加载key
        SessionHolder.addIdentity(path, password);
        machineSecretKeyDAO.insert(key);
        return key.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateSecretKey(MachineKeyRequest request) {
        MachineSecretKeyDO key = new MachineSecretKeyDO();
        Long id = request.getId();
        String password = request.getPassword();
        String fileBase64 = request.getFile();
        key.setId(id);
        key.setKeyName(request.getName());
        key.setDescription(request.getDescription());
        if (Strings.isBlank(fileBase64) && Strings.isBlank(password)) {
            return machineSecretKeyDAO.updateById(key);
        }
        // 移除原先的key
        MachineSecretKeyDO beforeKey = machineSecretKeyDAO.selectById(id);
        String beforePassword = beforeKey.getPassword();
        String beforeKeyFile = beforeKey.getSecretKeyPath();
        String beforeSecretKeyPath = MachineKeyService.getKeyPath(beforeKeyFile);
        SessionHolder.removeIdentity(beforeSecretKeyPath);
        Files1.delete(beforeSecretKeyPath);

        // 秘钥文件
        if (!Strings.isBlank(fileBase64)) {
            Files1.touch(beforeSecretKeyPath);
            byte[] keyFileData = Base64s.decode(Strings.bytes(fileBase64));
            FileWriters.writeFast(beforeSecretKeyPath, keyFileData);
        }

        // 密码
        if (!Strings.isBlank(password)) {
            key.setPassword(ValueMix.encrypt(password));
        }
        if (Strings.isBlank(password) && !Strings.isBlank(beforePassword)) {
            password = ValueMix.decrypt(password);
        }

        // 加载key
        SessionHolder.addIdentity(beforeSecretKeyPath, Strings.def(password, (String) null));
        return machineSecretKeyDAO.updateById(key);
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
        LambdaQueryWrapper<MachineSecretKeyDO> wrapper = new LambdaQueryWrapper<MachineSecretKeyDO>()
                .like(Objects.nonNull(request.getName()), MachineSecretKeyDO::getKeyName, request.getName())
                .like(Objects.nonNull(request.getDescription()), MachineSecretKeyDO::getDescription, request.getDescription())
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
                        row.setMountStatus(MountKeyStatus.MOUNT.getStatus());
                    } else {
                        row.setMountStatus(MountKeyStatus.UNMOUNT.getStatus());
                    }
                } else {
                    row.setMountStatus(MountKeyStatus.NOT_FOUND.getStatus());
                }
            }
        }
        return dataGrid;
    }

    @Override
    public Integer mountKey(Long id) {
        MachineSecretKeyDO key = Valid.notNull(machineSecretKeyDAO.selectById(id), "秘钥未找到");
        return this.mountOrUnmount(key, true);
    }

    @Override
    public Integer unmountKey(Long id) {
        MachineSecretKeyDO key = Valid.notNull(machineSecretKeyDAO.selectById(id), "秘钥未找到");
        return this.mountOrUnmount(key, false);
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
    private Integer mountOrUnmount(MachineSecretKeyDO key, boolean mount) {
        String path = key.getSecretKeyPath();
        String keyPath = MachineKeyService.getKeyPath(path);
        File file = new File(keyPath);
        if (!Files1.isFile(file)) {
            return MountKeyStatus.NOT_FOUND.getStatus();
        }
        if (mount) {
            // 挂载
            String password = key.getPassword();
            if (!Strings.isBlank(password)) {
                key.setPassword(ValueMix.encrypt(password));
            }
            // 加载key
            SessionHolder.addIdentity(keyPath, password);
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
            return MountKeyStatus.MOUNT.getStatus();
        } else {
            return MountKeyStatus.UNMOUNT.getStatus();
        }
    }

}
