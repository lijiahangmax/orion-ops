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
package com.orion.ops.controller;

import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.common.TreeMoveType;
import com.orion.ops.entity.request.machine.MachineGroupRelRequest;
import com.orion.ops.entity.request.machine.MachineGroupRequest;
import com.orion.ops.entity.vo.machine.MachineGroupTreeVO;
import com.orion.ops.service.api.MachineGroupRelService;
import com.orion.ops.service.api.MachineGroupService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 机器分组 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/23 16:17
 */
@Api(tags = "机器分组")
@RestController
@RestWrapper
@RequestMapping("/orion/api/machine-group")
public class MachineGroupController {

    @Resource
    private MachineGroupService machineGroupService;

    @Resource
    private MachineGroupRelService machineGroupRelService;

    @PostMapping("/add")
    @ApiOperation(value = "添加分组")
    public Long addGroup(@RequestBody MachineGroupRequest request) {
        Valid.notNull(request.getParentId());
        Valid.notBlank(request.getName());
        return machineGroupService.addGroup(request);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除分组")
    public Integer deleteGroup(@RequestBody MachineGroupRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return machineGroupService.deleteGroup(idList);
    }

    @PostMapping("/move")
    @ApiOperation(value = "移动分组")
    public HttpWrapper<?> moveGroup(@RequestBody MachineGroupRequest request) {
        Valid.allNotNull(request.getId(), request.getTargetId(), TreeMoveType.of(request.getMoveType()));
        machineGroupService.moveGroup(request);
        return HttpWrapper.ok();
    }

    @PostMapping("/rename")
    @ApiOperation(value = "修改分组名称")
    public Integer renameGroup(@RequestBody MachineGroupRequest request) {
        Long id = Valid.notNull(request.getId());
        String name = Valid.notBlank(request.getName());
        return machineGroupService.renameGroup(id, name);
    }

    @GetMapping("/tree")
    @ApiOperation(value = "获取机器分组树")
    public List<MachineGroupTreeVO> getRootTree() {
        return machineGroupService.getRootTree();
    }

    @PostMapping("/add-machine")
    @ApiOperation(value = "组内添加机器")
    public HttpWrapper<?> addMachineRelByGroup(@RequestBody MachineGroupRelRequest request) {
        Long groupId = Valid.notNull(request.getGroupId());
        List<Long> machineIdList = Valid.notEmpty(request.getMachineIdList());
        machineGroupRelService.addMachineRelByGroup(groupId, machineIdList);
        return HttpWrapper.ok();
    }

    @PostMapping("/delete-machine")
    @ApiOperation(value = "删除组内机器")
    public Integer deleteByGroupMachineId(@RequestBody MachineGroupRelRequest request) {
        List<Long> groupIdList = Valid.notEmpty(request.getGroupIdList());
        List<Long> machineIdList = Valid.notEmpty(request.getMachineIdList());
        return machineGroupRelService.deleteByGroupMachineId(groupIdList, machineIdList);
    }

}
