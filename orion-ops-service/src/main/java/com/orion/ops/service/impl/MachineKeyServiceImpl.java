package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.ObjectIds;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.EnvAttr;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.dao.MachineSecretKeyDAO;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.request.MachineKeyRequest;
import com.orion.ops.entity.vo.MachineSecretKeyVO;
import com.orion.ops.service.api.MachineKeyService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.ValueMix;
import com.orion.remote.channel.SessionHolder;
import com.orion.utils.Strings;
import com.orion.utils.codec.Base64s;
import com.orion.utils.io.FileWriters;
import com.orion.utils.io.Files1;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/5 11:20
 */
@Service("machineKeyService")
public class MachineKeyServiceImpl implements MachineKeyService {

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Resource
    private MachineSecretKeyDAO machineSecretKeyDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addUpdateSecretKey(MachineKeyRequest request) {
        MachineSecretKeyDO key = new MachineSecretKeyDO();
        key.setId(request.getId());
        key.setKeyName(request.getName());
        key.setPassword(request.getPassword());
        key.setDescription(request.getDescription());
        String path = Files1.getPath(EnvAttr.KEY_PATH.getValue() + "/" + ObjectIds.next() + "_id_rsa");
        key.setSecretKeyPath(path);
        Files1.touch(path);
        byte[] keyFileData = Base64s.decode(Strings.bytes(request.getFile()));
        FileWriters.writeFast(path, keyFileData);

        Long id = key.getId();
        if (id != null) {
            MachineSecretKeyDO beforeKey = machineSecretKeyDAO.selectById(id);
            // 移除key
            SessionHolder.removeIdentity(beforeKey.getSecretKeyPath());
            // 删除key
            Files1.delete(beforeKey.getSecretKeyPath());
        }
        String password = key.getPassword();
        if (!Strings.isBlank(password)) {
            key.setPassword(ValueMix.encrypt(password));
        }
        // 加载key
        SessionHolder.addIdentity(key.getSecretKeyPath(), password);
        if (id == null) {
            machineSecretKeyDAO.insert(key);
            return key.getId();
        } else {
            return (long) machineSecretKeyDAO.updateById(key);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer removeSecretKey(Long id) {
        MachineSecretKeyDO key = machineSecretKeyDAO.selectById(id);
        if (key == null) {
            return 0;
        }
        // 移除key
        SessionHolder.removeIdentity(key.getSecretKeyPath());
        // 删除key
        Files1.delete(key.getSecretKeyPath());
        // 移除机器key
        machineInfoDAO.setKeyIdWithNull(id);
        return machineSecretKeyDAO.deleteById(id);
    }

    @Override
    public DataGrid<MachineSecretKeyVO> listKeys(MachineKeyRequest request) {
        LambdaQueryWrapper<MachineSecretKeyDO> wrapper = new LambdaQueryWrapper<MachineSecretKeyDO>()
                .like(Objects.nonNull(request.getName()), MachineSecretKeyDO::getKeyName, request.getName())
                .like(Objects.nonNull(request.getDescription()), MachineSecretKeyDO::getDescription, request.getDescription())
                .orderByDesc(MachineSecretKeyDO::getCreateTime);
        return DataQuery.of(machineSecretKeyDAO)
                .wrapper(wrapper)
                .page(request)
                .dataGrid(MachineSecretKeyVO.class);
    }

}
