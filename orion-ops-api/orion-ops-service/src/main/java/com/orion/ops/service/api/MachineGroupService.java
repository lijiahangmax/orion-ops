package com.orion.ops.service.api;

import com.orion.ops.entity.request.machine.MachineGroupRequest;
import com.orion.ops.entity.vo.machine.MachineGroupTreeVO;

import java.util.List;

/**
 * 机器分组服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/23 16:10
 */
public interface MachineGroupService {

    /**
     * 添加组
     *
     * @param request request
     * @return if
     */
    Long addGroup(MachineGroupRequest request);

    /**
     * 删除组
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteGroup(List<Long> idList);

    /**
     * 移动组
     *
     * @param request request
     * @return effect
     */
    Integer moveGroup(MachineGroupRequest request);

    /**
     * 重命名
     *
     * @param id   id
     * @param name name
     * @return effect
     */
    Integer renameGroup(Long id, String name);

    /**
     * 获取机器分组树
     *
     * @return tree
     */
    List<MachineGroupTreeVO> getRootTree();

}
