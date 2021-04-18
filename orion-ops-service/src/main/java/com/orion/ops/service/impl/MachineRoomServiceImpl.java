package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.Pager;
import com.orion.ops.consts.Const;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.dao.MachineRoomDAO;
import com.orion.ops.entity.domain.MachineRoomDO;
import com.orion.ops.entity.request.MachineRoomRequest;
import com.orion.ops.entity.vo.MachineRoomVO;
import com.orion.ops.service.api.MachineRoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        Pager<MachineRoomVO> pager = Pager.of(request);
        LambdaQueryWrapper<MachineRoomDO> wrapper = new LambdaQueryWrapper<MachineRoomDO>()
                .like(Objects.nonNull(request.getName()), MachineRoomDO::getRoomName, request.getName())
                .like(Objects.nonNull(request.getTag()), MachineRoomDO::getRoomTag, request.getTag())
                .like(Objects.nonNull(request.getDescription()), MachineRoomDO::getDescription, request.getDescription())
                .eq(Objects.nonNull(request.getManagerId()), MachineRoomDO::getManagerId, request.getManagerId())
                .eq(Objects.nonNull(request.getStatus()), MachineRoomDO::getRoomStatus, request.getStatus())
                .orderByDesc(MachineRoomDO::getCreateTime);
        Integer count = machineRoomDAO.selectCount(wrapper);
        pager.setTotal(count);
        boolean next = pager.hasMoreData();
        if (next) {
            wrapper.last(pager.getSql());
            List<MachineRoomVO> rows = machineRoomDAO.selectList(wrapper).stream()
                    .map(p -> {
                        MachineRoomVO vo = new MachineRoomVO();
                        vo.setId(p.getId());
                        vo.setName(p.getRoomName());
                        vo.setTag(p.getRoomTag());
                        vo.setDescription(p.getDescription());
                        vo.setStatus(p.getRoomStatus());
                        vo.setManagerId(p.getManagerId());
                        vo.setCreateTime(p.getCreateTime());
                        vo.setUpdateTime(p.getUpdateTime());
                        return vo;
                    }).collect(Collectors.toList());
            pager.setRows(rows);
        }
        return DataGrid.of(pager);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteMachineRoom(MachineRoomRequest request) {
        Long id = request.getId();
        machineInfoDAO.setRoomIdWithNull(id);
        return machineInfoDAO.deleteById(id);
    }

}
