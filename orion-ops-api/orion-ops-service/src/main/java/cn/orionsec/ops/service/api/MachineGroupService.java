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
package cn.orionsec.ops.service.api;

import cn.orionsec.ops.entity.request.machine.MachineGroupRequest;
import cn.orionsec.ops.entity.vo.machine.MachineGroupTreeVO;

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
     * @return id
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
     */
    void moveGroup(MachineGroupRequest request);

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
