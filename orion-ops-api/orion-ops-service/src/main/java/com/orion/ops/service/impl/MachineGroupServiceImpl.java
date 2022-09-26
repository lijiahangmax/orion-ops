package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.Strings;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.KeyConst;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.dao.MachineGroupDAO;
import com.orion.ops.entity.domain.MachineGroupDO;
import com.orion.ops.entity.request.machine.MachineGroupRequest;
import com.orion.ops.entity.vo.machine.MachineGroupTreeVO;
import com.orion.ops.service.api.MachineGroupRelService;
import com.orion.ops.service.api.MachineGroupService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
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
    public Integer moveGroup(MachineGroupRequest request) {
        Long id = request.getId();
        Long prevId = request.getPrevId();
        // 查询分组信息
        MachineGroupDO moveGroup = machineGroupDAO.selectById(id);
        MachineGroupDO prevGroup = machineGroupDAO.selectById(prevId);
        Valid.notNull(moveGroup, MessageConst.UNKNOWN_DATA);
        Valid.notNull(prevGroup, MessageConst.UNKNOWN_DATA);
        Long prevParentId = prevGroup.getParentId();
        Integer prevSort = prevGroup.getSort();
        // 检查同级是否重名
        this.checkNamePresent(moveGroup.getId(), prevParentId, moveGroup.getGroupName());
        // 将同级sort增大
        machineGroupDAO.incrementSort(prevParentId, prevSort);
        // 修改移动分组信息
        MachineGroupDO update = new MachineGroupDO();
        update.setId(id);
        update.setParentId(prevParentId);
        update.setSort(prevSort + 1);
        int effect = machineGroupDAO.updateById(update);
        // 清除缓存
        this.clearGroupCache(false);
        return effect;
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
        String cacheData = redisTemplate.opsForValue().get(KeyConst.MACHINE_GROUP_DATA_KEY);
        if (!Strings.isBlank(cacheData)) {
            return JSON.parseArray(cacheData, MachineGroupTreeVO.class);
        }
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
        Long parentId = parent.getParentId();
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
