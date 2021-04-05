package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.PageRequest;
import com.orion.lang.wrapper.Pager;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.dao.MachineSecretKeyDAO;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.vo.MachineSecretKeyVO;
import com.orion.ops.service.api.MachineKeyService;
import com.orion.remote.channel.SessionHolder;
import com.orion.utils.io.Files1;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
    public Long addSecretKey(MachineSecretKeyDO key) {
        // 新增key
        machineSecretKeyDAO.insert(key);
        // 加载key
        SessionHolder.addIdentity(key.getSecretKeyPath(), key.getPassword());
        return key.getId();
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
    @Transactional(rollbackFor = Exception.class)
    public Integer updateSecretKey(MachineSecretKeyDO key) {
        MachineSecretKeyDO beforeKey = machineSecretKeyDAO.selectById(key.getId());
        // 移除key
        SessionHolder.removeIdentity(beforeKey.getSecretKeyPath());
        // 删除key
        Files1.delete(beforeKey.getSecretKeyPath());
        // 修改key
        int effect = machineSecretKeyDAO.updateById(key);
        // 加载key
        SessionHolder.addIdentity(key.getSecretKeyPath(), key.getPassword());
        return effect;
    }

    @Override
    public DataGrid<MachineSecretKeyVO> listKeys(PageRequest page) {
        Pager<MachineSecretKeyVO> pager = Pager.of(page);
        LambdaQueryWrapper<MachineSecretKeyDO> wrapper = new LambdaQueryWrapper<MachineSecretKeyDO>()
                .orderByDesc(MachineSecretKeyDO::getCreateTime);
        Integer count = machineSecretKeyDAO.selectCount(wrapper);
        pager.setTotal(count);
        boolean next = pager.hasMoreData();
        if (next) {
            wrapper.last(pager.getSql());
            List<MachineSecretKeyVO> rows = machineSecretKeyDAO.selectList(wrapper).stream()
                    .map(p -> {
                        MachineSecretKeyVO vo = new MachineSecretKeyVO();
                        vo.setId(p.getId());
                        vo.setName(p.getKeyName());
                        vo.setPath(p.getSecretKeyPath());
                        vo.setDescription(p.getDescription());
                        vo.setCreateTime(p.getCreateTime());
                        return vo;
                    }).collect(Collectors.toList());
            pager.setRows(rows);
        }
        return DataGrid.of(pager);
    }

}
