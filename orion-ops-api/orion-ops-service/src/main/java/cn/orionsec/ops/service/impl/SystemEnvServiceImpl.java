/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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

import cn.orionsec.kit.lang.define.collect.MutableLinkedHashMap;
import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.collect.Lists;
import cn.orionsec.kit.lang.utils.collect.Maps;
import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.env.EnvConst;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.history.HistoryOperator;
import cn.orionsec.ops.constant.history.HistoryValueType;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.dao.SystemEnvDAO;
import cn.orionsec.ops.entity.domain.SystemEnvDO;
import cn.orionsec.ops.entity.request.system.SystemEnvRequest;
import cn.orionsec.ops.entity.vo.system.SystemEnvVO;
import cn.orionsec.ops.service.api.ApplicationRepositoryService;
import cn.orionsec.ops.service.api.HistoryValueService;
import cn.orionsec.ops.service.api.SystemEnvService;
import cn.orionsec.ops.utils.DataQuery;
import cn.orionsec.ops.utils.EventParamsHolder;
import cn.orionsec.ops.utils.Valid;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 系统环境变量服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 17:06
 */
@Service("systemEnvService")
public class SystemEnvServiceImpl implements SystemEnvService {

    @Resource
    private SystemEnvDAO systemEnvDAO;

    @Resource
    private HistoryValueService historyValueService;

    @Resource
    private ApplicationRepositoryService applicationRepositoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addEnv(SystemEnvRequest request) {
        // 查询
        String key = request.getKey();
        // 重复检查
        SystemEnvDO env = systemEnvDAO.selectOneRel(key);
        // 修改
        if (env != null) {
            SpringHolder.getBean(SystemEnvService.class).updateEnv(env, request);
            return env.getId();
        }
        // 新增
        SystemEnvDO insert = new SystemEnvDO();
        insert.setAttrKey(key);
        insert.setAttrValue(request.getValue());
        insert.setDescription(request.getDescription());
        systemEnvDAO.insert(insert);
        // 插入历史值
        Long id = insert.getId();
        historyValueService.addHistory(id, HistoryValueType.SYSTEM_ENV, HistoryOperator.ADD, null, request.getValue());
        // 设置日志参数
        EventParamsHolder.addParams(insert);
        return id;
    }

    @Override
    public Integer updateEnv(SystemEnvRequest request) {
        // 查询
        Long id = request.getId();
        SystemEnvDO before = systemEnvDAO.selectById(id);
        Valid.notNull(before, MessageConst.ENV_ABSENT);
        return SpringHolder.getBean(SystemEnvService.class).updateEnv(before, request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateEnv(SystemEnvDO before, SystemEnvRequest request) {
        // 检查是否修改了值
        Long id = before.getId();
        String key = before.getAttrKey();
        String beforeValue = before.getAttrValue();
        String afterValue = request.getValue();
        if (Const.IS_DELETED.equals(before.getDeleted())) {
            // 设置新增历史值
            historyValueService.addHistory(id, HistoryValueType.SYSTEM_ENV, HistoryOperator.ADD, null, afterValue);
            // 恢复
            systemEnvDAO.setDeleted(id, Const.NOT_DELETED);
        } else if (afterValue != null && !afterValue.equals(beforeValue)) {
            // 检查是否修改了值 增加历史值
            historyValueService.addHistory(id, HistoryValueType.SYSTEM_ENV, HistoryOperator.UPDATE, beforeValue, afterValue);
        }
        // 同步更新对象
        SystemEnvAttr env = SystemEnvAttr.of(key);
        if (env != null) {
            env.setValue(afterValue);
            if (afterValue != null && !afterValue.equals(beforeValue)) {
                if (SystemEnvAttr.REPO_PATH.equals(env)) {
                    // 如果修改的是仓库路径则修改仓库状态
                    applicationRepositoryService.syncRepositoryStatus();
                }
            }
        }
        // 修改
        SystemEnvDO update = new SystemEnvDO();
        update.setId(id);
        update.setAttrKey(key);
        update.setAttrValue(afterValue);
        update.setDescription(request.getDescription());
        update.setUpdateTime(new Date());
        // 设置日志参数
        EventParamsHolder.addParams(update);
        return systemEnvDAO.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteEnv(List<Long> idList) {
        int effect = 0;
        for (Long id : idList) {
            // 获取元数据
            SystemEnvDO env = systemEnvDAO.selectById(id);
            Valid.notNull(env, MessageConst.ENV_ABSENT);
            String key = env.getAttrKey();
            Valid.isTrue(SystemEnvAttr.of(key) == null, "{} " + MessageConst.FORBID_DELETE, key);
            // 删除
            effect += systemEnvDAO.deleteById(id);
            // 插入历史值
            historyValueService.addHistory(id, HistoryValueType.SYSTEM_ENV, HistoryOperator.DELETE, env.getAttrValue(), null);
        }
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID_LIST, idList);
        EventParamsHolder.addParam(EventKeys.COUNT, effect);
        return effect;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveEnv(Map<String, String> env) {
        SystemEnvService self = SpringHolder.getBean(SystemEnvService.class);
        // 倒排
        List<Map.Entry<String, String>> entries = Lists.newList(env.entrySet());
        int size = entries.size();
        for (int i = size - 1; i >= 0; i--) {
            // 更新
            Map.Entry<String, String> entry = entries.get(i);
            SystemEnvRequest request = new SystemEnvRequest();
            request.setKey(entry.getKey());
            request.setValue(entry.getValue());
            self.addEnv(request);
        }
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.COUNT, size);
    }

    @Override
    public DataGrid<SystemEnvVO> listEnv(SystemEnvRequest request) {
        LambdaQueryWrapper<SystemEnvDO> wrapper = new LambdaQueryWrapper<SystemEnvDO>()
                .like(Strings.isNotBlank(request.getKey()), SystemEnvDO::getAttrKey, request.getKey())
                .like(Strings.isNotBlank(request.getValue()), SystemEnvDO::getAttrValue, request.getValue())
                .like(Strings.isNotBlank(request.getDescription()), SystemEnvDO::getDescription, request.getDescription())
                .eq(SystemEnvDO::getSystemEnv, Const.NOT_SYSTEM)
                .orderByDesc(SystemEnvDO::getUpdateTime);
        return DataQuery.of(systemEnvDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(SystemEnvVO.class);
    }

    @Override
    public SystemEnvVO getEnvDetail(Long id) {
        SystemEnvDO env = systemEnvDAO.selectById(id);
        Valid.notNull(env, MessageConst.UNKNOWN_DATA);
        return Converts.to(env, SystemEnvVO.class);
    }

    @Override
    public String getEnvValue(String env) {
        LambdaQueryWrapper<SystemEnvDO> wrapper = new LambdaQueryWrapper<SystemEnvDO>()
                .eq(SystemEnvDO::getAttrKey, env)
                .last(Const.LIMIT_1);
        return Optional.ofNullable(systemEnvDAO.selectOne(wrapper))
                .map(SystemEnvDO::getAttrValue)
                .orElse(null);
    }

    @Override
    public SystemEnvDO selectByName(String env) {
        LambdaQueryWrapper<SystemEnvDO> wrapper = new LambdaQueryWrapper<SystemEnvDO>()
                .eq(SystemEnvDO::getAttrKey, env)
                .last(Const.LIMIT_1);
        return systemEnvDAO.selectOne(wrapper);
    }

    @Override
    public MutableLinkedHashMap<String, String> getSystemEnv() {
        MutableLinkedHashMap<String, String> env = Maps.newMutableLinkedMap();
        LambdaQueryWrapper<SystemEnvDO> wrapper = new LambdaQueryWrapper<SystemEnvDO>()
                .eq(SystemEnvDO::getSystemEnv, Const.NOT_SYSTEM)
                .orderByAsc(SystemEnvDO::getId);
        systemEnvDAO.selectList(wrapper).forEach(e -> env.put(e.getAttrKey(), e.getAttrValue()));
        return env;
    }

    @Override
    public MutableLinkedHashMap<String, String> getFullSystemEnv() {
        MutableLinkedHashMap<String, String> env = Maps.newMutableLinkedMap();
        LambdaQueryWrapper<SystemEnvDO> wrapper = new LambdaQueryWrapper<SystemEnvDO>()
                .eq(SystemEnvDO::getSystemEnv, Const.NOT_SYSTEM)
                .orderByAsc(SystemEnvDO::getId);
        systemEnvDAO.selectList(wrapper).forEach(e -> env.put(EnvConst.SYSTEM_PREFIX + e.getAttrKey(), e.getAttrValue()));
        return env;
    }

}
