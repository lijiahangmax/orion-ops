package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.PageRequest;
import com.orion.lang.wrapper.Pager;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.dao.MachineProxyDAO;
import com.orion.ops.entity.domain.MachineProxyDO;
import com.orion.ops.entity.vo.MachineProxyVO;
import com.orion.ops.service.api.MachineProxyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
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
    public Long addProxy(MachineProxyDO proxy) {
        machineProxyDAO.insert(proxy);
        return proxy.getId();
    }

    @Override
    public DataGrid<MachineProxyVO> listProxy(PageRequest request) {
        Pager<MachineProxyVO> pager = Pager.of(request);
        LambdaQueryWrapper<MachineProxyDO> wrapper = new LambdaQueryWrapper<MachineProxyDO>()
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
                        vo.setDescription(p.getDescription());
                        vo.setCreateTime(p.getCreateTime());
                        return vo;
                    }).collect(Collectors.toList());
            pager.setRows(rows);
        }
        return DataGrid.of(pager);
    }

    @Override
    public Integer updateProxy(MachineProxyDO proxy) {
        return machineProxyDAO.updateById(proxy);
    }

    @Override
    public Integer deleteProxy(Long id) {
        machineInfoDAO.setProxyIdWithNull(id);
        return machineProxyDAO.deleteById(id);
    }

}
