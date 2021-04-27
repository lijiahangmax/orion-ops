package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.Pager;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.dao.MachineProxyDAO;
import com.orion.ops.entity.domain.MachineProxyDO;
import com.orion.ops.entity.request.MachineProxyRequest;
import com.orion.ops.entity.vo.MachineProxyVO;
import com.orion.ops.service.api.MachineProxyService;
import com.orion.ops.utils.ValueMix;
import com.orion.utils.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 代理服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 22:15
 */
@Service("machineProxyService")
public class MachineProxyServiceImpl implements MachineProxyService {

    @Resource
    private MachineProxyDAO machineProxyDAO;

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Override
    public Long addUpdateProxy(MachineProxyRequest request) {
        Long id = request.getId();
        MachineProxyDO proxy = new MachineProxyDO();
        proxy.setId(id);
        proxy.setProxyHost(request.getHost());
        proxy.setProxyPort(request.getPort());
        proxy.setProxyUsername(request.getUsername());
        proxy.setProxyType(request.getType());
        String password = request.getPassword();
        if (!Strings.isBlank(password)) {
            proxy.setProxyPassword(ValueMix.encrypt(password));
        }
        proxy.setDescription(request.getDescription());
        if (id == null) {
            machineProxyDAO.insert(proxy);
            return proxy.getId();
        } else {
            return (long) machineProxyDAO.updateById(proxy);
        }
    }

    @Override
    public DataGrid<MachineProxyVO> listProxy(MachineProxyRequest request) {
        Pager<MachineProxyVO> pager = Pager.of(request);
        LambdaQueryWrapper<MachineProxyDO> wrapper = new LambdaQueryWrapper<MachineProxyDO>()
                .like(Objects.nonNull(request.getHost()), MachineProxyDO::getProxyHost, request.getHost())
                .like(Objects.nonNull(request.getUsername()), MachineProxyDO::getProxyUsername, request.getUsername())
                .like(Objects.nonNull(request.getDescription()), MachineProxyDO::getDescription, request.getDescription())
                .eq(Objects.nonNull(request.getPort()), MachineProxyDO::getProxyPort, request.getPort())
                .eq(Objects.nonNull(request.getType()), MachineProxyDO::getProxyType, request.getType())
                .orderByDesc(MachineProxyDO::getCreateTime);
        Integer count = machineProxyDAO.selectCount(wrapper);
        pager.setTotal(count);
        boolean next = pager.hasMoreData();
        if (next) {
            wrapper.last(pager.getSql());
            List<MachineProxyVO> rows = machineProxyDAO.selectList(wrapper).stream()
                    .map(p -> {
                        MachineProxyVO vo = new MachineProxyVO();
                        vo.setId(p.getId());
                        vo.setHost(p.getProxyHost());
                        vo.setPort(p.getProxyPort());
                        vo.setUsername(p.getProxyUsername());
                        vo.setType(p.getProxyType());
                        vo.setDescription(p.getDescription());
                        vo.setCreateTime(p.getCreateTime());
                        return vo;
                    }).collect(Collectors.toList());
            pager.setRows(rows);
        }
        return DataGrid.of(pager);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteProxy(Long id) {
        machineInfoDAO.setProxyIdWithNull(id);
        return machineProxyDAO.deleteById(id);
    }

}
