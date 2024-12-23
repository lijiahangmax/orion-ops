/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.dao.MachineInfoDAO;
import cn.orionsec.ops.dao.MachineProxyDAO;
import cn.orionsec.ops.entity.domain.MachineProxyDO;
import cn.orionsec.ops.entity.request.machine.MachineProxyRequest;
import cn.orionsec.ops.entity.vo.machine.MachineProxyVO;
import cn.orionsec.ops.service.api.MachineProxyService;
import cn.orionsec.ops.utils.DataQuery;
import cn.orionsec.ops.utils.EventParamsHolder;
import cn.orionsec.ops.utils.Valid;
import cn.orionsec.ops.utils.ValueMix;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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
    public Long addProxy(MachineProxyRequest request) {
        MachineProxyDO proxy = new MachineProxyDO();
        proxy.setProxyHost(request.getHost());
        proxy.setProxyPort(request.getPort());
        proxy.setProxyType(request.getType());
        proxy.setProxyUsername(request.getUsername());
        String password = request.getPassword();
        if (!Strings.isBlank(password)) {
            proxy.setProxyPassword(ValueMix.encrypt(password));
        }
        proxy.setDescription(request.getDescription());
        // 插入
        machineProxyDAO.insert(proxy);
        // 设置日志参数
        EventParamsHolder.addParams(proxy);
        return proxy.getId();
    }

    @Override
    public Integer updateProxy(MachineProxyRequest request) {
        // 查询
        Long id = request.getId();
        MachineProxyDO beforeProxy = machineProxyDAO.selectById(id);
        Valid.notNull(beforeProxy, MessageConst.UNKNOWN_DATA);
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
        // 修改
        int effect = machineProxyDAO.updateById(proxy);
        // 设置日志参数
        EventParamsHolder.addParams(proxy);
        EventParamsHolder.addParam(EventKeys.HOST, beforeProxy.getProxyHost());
        return effect;
    }

    @Override
    public DataGrid<MachineProxyVO> listProxy(MachineProxyRequest request) {
        LambdaQueryWrapper<MachineProxyDO> wrapper = new LambdaQueryWrapper<MachineProxyDO>()
                .like(Strings.isNotBlank(request.getHost()), MachineProxyDO::getProxyHost, request.getHost())
                .like(Strings.isNotBlank(request.getUsername()), MachineProxyDO::getProxyUsername, request.getUsername())
                .like(Strings.isNotBlank(request.getDescription()), MachineProxyDO::getDescription, request.getDescription())
                .eq(Objects.nonNull(request.getPort()), MachineProxyDO::getProxyPort, request.getPort())
                .eq(Objects.nonNull(request.getType()), MachineProxyDO::getProxyType, request.getType())
                .orderByDesc(MachineProxyDO::getCreateTime);
        return DataQuery.of(machineProxyDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(MachineProxyVO.class);
    }

    @Override
    public MachineProxyVO getProxyDetail(Long id) {
        MachineProxyDO proxy = machineProxyDAO.selectById(id);
        Valid.notNull(proxy, MessageConst.UNKNOWN_DATA);
        return Converts.to(proxy, MachineProxyVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteProxy(List<Long> idList) {
        int effect = 0;
        for (Long id : idList) {
            machineInfoDAO.setProxyIdWithNull(id);
            effect += machineProxyDAO.deleteById(id);
        }
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID_LIST, idList);
        EventParamsHolder.addParam(EventKeys.COUNT, effect);
        return effect;
    }

}
