package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.dao.MachineRoomDAO;
import com.orion.ops.entity.domain.MachineRoomDO;
import com.orion.ops.entity.request.MachineRoomRequest;
import com.orion.ops.entity.vo.MachineRoomVO;
import com.orion.ops.service.api.MachineRoomService;
import com.orion.ops.utils.DataQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 机房服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/14 20:15
 */
@Service("machineRoomService")
public class MachineRoomServiceImpl implements MachineRoomService {

    @Resource
    private MachineRoomDAO machineRoomDAO;

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Override
    public Long addUpdateMachineRoom(MachineRoomRequest request) {
        Long id = request.getId();
        MachineRoomDO entity = new MachineRoomDO();
        entity.setId(id);
        entity.setRoomName(request.getName());
        entity.setRoomTag(request.getTag());
        entity.setDescription(request.getDescription());
        entity.setManagerId(request.getManagerId());
        if (id == null) {
            // 新增
            entity.setRoomStatus(Const.ENABLE);
            machineRoomDAO.insert(entity);
            return entity.getId();
        } else {
            // 修改
            entity.setRoomStatus(request.getStatus());
            return (long) machineRoomDAO.updateById(entity);
        }
    }

    @Override
    public DataGrid<MachineRoomVO> listMachineRoom(MachineRoomRequest request) {
        LambdaQueryWrapper<MachineRoomDO> wrapper = new LambdaQueryWrapper<MachineRoomDO>()
                .like(Objects.nonNull(request.getName()), MachineRoomDO::getRoomName, request.getName())
                .like(Objects.nonNull(request.getTag()), MachineRoomDO::getRoomTag, request.getTag())
                .like(Objects.nonNull(request.getDescription()), MachineRoomDO::getDescription, request.getDescription())
                .eq(Objects.nonNull(request.getManagerId()), MachineRoomDO::getManagerId, request.getManagerId())
                .eq(Objects.nonNull(request.getStatus()), MachineRoomDO::getRoomStatus, request.getStatus())
                .orderByDesc(MachineRoomDO::getCreateTime);
        return DataQuery.of(machineRoomDAO)
                .wrapper(wrapper)
                .page(request)
                .dataGrid(MachineRoomVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteMachineRoom(List<Long> ids) {
        int effect = 0;
        for (Long id : ids) {
            machineInfoDAO.setRoomIdWithNull(id);
            effect += machineRoomDAO.deleteById(id);
        }
        return effect;
    }

    @Override
    public Integer updateStatus(List<Long> ids, Integer status) {
        int effect = 0;
        for (Long id : ids) {
            MachineRoomDO entity = new MachineRoomDO();
            entity.setId(id);
            entity.setRoomStatus(status);
            effect += machineRoomDAO.updateById(entity);
        }
        return effect;
    }

}
