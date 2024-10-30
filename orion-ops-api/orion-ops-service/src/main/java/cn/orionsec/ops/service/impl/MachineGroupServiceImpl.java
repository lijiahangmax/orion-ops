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

import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.KeyConst;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.common.TreeMoveType;
import cn.orionsec.ops.dao.MachineGroupDAO;
import cn.orionsec.ops.entity.domain.MachineGroupDO;
import cn.orionsec.ops.entity.request.machine.MachineGroupRequest;
import cn.orionsec.ops.entity.vo.machine.MachineGroupTreeVO;
import cn.orionsec.ops.service.api.MachineGroupRelService;
import cn.orionsec.ops.service.api.MachineGroupService;
import cn.orionsec.ops.utils.DataQuery;
import cn.orionsec.ops.utils.Valid;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.Objects1;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 机器分组服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/23 16:11
 */
@Service("machineGroupService")
public class MachineGroupServiceImpl implements MachineGroupService {

    @Resource
    private MachineGroupDAO machineGroupDAO;

    @Resource
    private MachineGroupRelService machineGroupRelService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addGroup(MachineGroupRequest request) {
        Long parentId = request.getParentId();
        String name = request.getName();
        // 检查同级是否重名
        this.checkNamePresent(null, parentId, name);
        // 将同级sort增大
        machineGroupDAO.incrementSort(parentId, null);
        // 插入
        MachineGroupDO entity = new MachineGroupDO();
        entity.setParentId(parentId);
        entity.setGroupName(name);
        entity.setSort(Const.DEFAULT_TREE_SORT);
        machineGroupDAO.insert(entity);
        // 清除缓存
        this.clearGroupCache(false);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteGroup(List<Long> idList) {
        // 删除分组
        int effect = machineGroupDAO.deleteBatchIds(idList);
        // 删除分组关联机器
        effect += machineGroupRelService.deleteByGroupIdList(idList);
        // 清除缓存
        this.clearGroupCache(true);
        return effect;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveGroup(MachineGroupRequest request) {
        Long id = request.getId();
        Long targetId = request.getTargetId();
        // 查询分组信息
        MachineGroupDO moveGroup = machineGroupDAO.selectById(id);
        MachineGroupDO targetGroup = machineGroupDAO.selectById(targetId);
        Valid.notNull(moveGroup, MessageConst.UNKNOWN_DATA);
        Valid.notNull(targetGroup, MessageConst.UNKNOWN_DATA);
        String moveGroupName = moveGroup.getGroupName();
        Integer targetSort = targetGroup.getSort();
        Long targetParentId = targetGroup.getParentId();
        switch (TreeMoveType.of(request.getMoveType())) {
            case IN_TOP: {
                // 移动到内层 上面 检查重名
                this.checkNamePresent(id, targetId, moveGroupName);
                // 将所有sort往下移动
                machineGroupDAO.incrementSort(targetId, null);
                // 修改parentId
                MachineGroupDO update = new MachineGroupDO();
                update.setId(id);
                update.setParentId(targetId);
                update.setSort(Const.DEFAULT_TREE_SORT);
                machineGroupDAO.updateById(update);
                break;
            }
            case IN_BOTTOM: {
                // 移动到内层 下面 检查重名
                this.checkNamePresent(id, targetId, moveGroupName);
                // 获取最大sort
                Integer maxSort = Objects1.def(machineGroupDAO.getMaxSort(targetId), Const.N_0);
                // 修改parentId
                MachineGroupDO update = new MachineGroupDO();
                update.setId(id);
                update.setParentId(targetId);
                update.setSort(maxSort + 1);
                machineGroupDAO.updateById(update);
                break;
            }
            case PREV: {
                // 移动到节点 上面 检查重名
                this.checkNamePresent(id, targetParentId, moveGroupName);
                // 将以下的sort往下移动
                machineGroupDAO.incrementSort(targetParentId, targetSort - 1);
                // 修改parentId
                MachineGroupDO update = new MachineGroupDO();
                update.setId(id);
                update.setParentId(targetParentId);
                update.setSort(targetSort);
                machineGroupDAO.updateById(update);
                break;
            }
            case NEXT: {
                // 移动到节点 下面 检查重名
                this.checkNamePresent(id, targetParentId, moveGroupName);
                // 将自己及以下的sort往下移动
                machineGroupDAO.incrementSort(targetParentId, targetSort);
                // 修改parentId
                MachineGroupDO update = new MachineGroupDO();
                update.setId(id);
                update.setParentId(targetParentId);
                update.setSort(targetSort + 1);
                machineGroupDAO.updateById(update);
                break;
            }
            default:
                break;
        }
        // 清除缓存
        this.clearGroupCache(false);
    }

    @Override
    public Integer renameGroup(Long id, String name) {
        // 查询分组信息
        MachineGroupDO group = machineGroupDAO.selectById(id);
        Valid.notNull(group, MessageConst.UNKNOWN_DATA);
        // 检查名称重复
        this.checkNamePresent(id, group.getParentId(), name);
        // 更新
        MachineGroupDO update = new MachineGroupDO();
        update.setId(id);
        update.setGroupName(name);
        int effect = machineGroupDAO.updateById(update);
        // 清除缓存
        this.clearGroupCache(false);
        return effect;
    }

    @Override
    public List<MachineGroupTreeVO> getRootTree() {
        // 查询缓存
        // String cacheData = redisTemplate.opsForValue().get(KeyConst.MACHINE_GROUP_DATA_KEY);
        // if (!Strings.isBlank(cacheData)) {
        //     return JSON.parseArray(cacheData, MachineGroupTreeVO.class);
        // }
        // 查询所有节点
        List<MachineGroupTreeVO> groups = DataQuery.of(machineGroupDAO).list(MachineGroupTreeVO.class);
        if (groups.isEmpty()) {
            return null;
        }
        // 构建树
        MachineGroupTreeVO root = new MachineGroupTreeVO();
        root.setId(Const.ROOT_TREE_ID);
        this.setTreeChildrenNode(root, groups);
        List<MachineGroupTreeVO> children = root.getChildren();
        // 设置缓存
        redisTemplate.opsForValue().set(KeyConst.MACHINE_GROUP_DATA_KEY,
                JSON.toJSONString(children),
                KeyConst.MACHINE_GROUP_DATA_EXPIRE,
                TimeUnit.SECONDS);
        return children;
    }

    /**
     * 获取tree子节点
     *
     * @param parent parent
     * @param groups groups
     */
    public void setTreeChildrenNode(MachineGroupTreeVO parent, List<MachineGroupTreeVO> groups) {
        // 获取子节点
        Long parentId = parent.getId();
        List<MachineGroupTreeVO> child = groups.stream()
                .filter(g -> g.getParentId().equals(parentId))
                .sorted(Comparator.comparing(MachineGroupTreeVO::getSort))
                .collect(Collectors.toList());
        // 移除
        groups.removeIf(g -> g.getParentId().equals(parentId));
        parent.setChildren(child);
        child.forEach(g -> this.setTreeChildrenNode(g, groups));
    }

    /**
     * 检查是否存在
     *
     * @param id       id
     * @param parentId parentId
     * @param name     name
     */
    private void checkNamePresent(Long id, Long parentId, String name) {
        LambdaQueryWrapper<MachineGroupDO> presentWrapper = new LambdaQueryWrapper<MachineGroupDO>()
                .ne(id != null, MachineGroupDO::getId, id)
                .eq(MachineGroupDO::getParentId, parentId)
                .eq(MachineGroupDO::getGroupName, name);
        boolean present = DataQuery.of(machineGroupDAO).wrapper(presentWrapper).present();
        Valid.isTrue(!present, MessageConst.NAME_PRESENT);
    }

    /**
     * 清理分组缓存
     *
     * @param clearRel 是否清除关联
     */
    private void clearGroupCache(boolean clearRel) {
        redisTemplate.delete(KeyConst.MACHINE_GROUP_DATA_KEY);
        if (clearRel) {
            machineGroupRelService.clearGroupRelCache();
        }
    }

}
