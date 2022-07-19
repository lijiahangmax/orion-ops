package com.orion.ops.controller;

import com.alibaba.fastjson.JSON;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Exceptions;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.MachineKeyRequest;
import com.orion.ops.entity.vo.MachineSecretKeyVO;
import com.orion.ops.service.api.MachineKeyService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 机器秘钥 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 23:10
 */
@Api(tags = "机器秘钥")
@RestController
@RestWrapper
@RequestMapping("/orion/api/key")
@Slf4j
public class MachineKeyController {

    @Resource
    private MachineKeyService machineKeyService;

    @PostMapping("/add")
    @ApiOperation(value = "添加机器秘钥")
    @EventLog(EventType.ADD_MACHINE_KEY)
    public Long addKey(@RequestBody MachineKeyRequest request) {
        Valid.notBlank(request.getName());
        Valid.notBlank(request.getPassword());
        Valid.notBlank(request.getFile());
        try {
            return machineKeyService.addSecretKey(request);
        } catch (Exception e) {
            log.error("添加秘钥失败 {} {}", JSON.toJSONString(request), e);
            throw Exceptions.app(MessageConst.ADD_SECRET_KEY_ERROR, e);
        }
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新机器秘钥")
    @EventLog(EventType.UPDATE_MACHINE_KEY)
    public Integer updateKey(@RequestBody MachineKeyRequest request) {
        Valid.notNull(request.getId());
        try {
            return machineKeyService.updateSecretKey(request);
        } catch (Exception e) {
            log.error("修改秘钥失败 {} {}", JSON.toJSONString(request), e);
            throw Exceptions.app(MessageConst.UPDATE_SECRET_KEY_ERROR, e);
        }
    }

    @PostMapping("/remove")
    @ApiOperation(value = "删除机器秘钥")
    @EventLog(EventType.DELETE_MACHINE_KEY)
    public Integer removeKey(@RequestBody MachineKeyRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        try {
            return machineKeyService.removeSecretKey(idList);
        } catch (Exception e) {
            log.error("删除秘钥失败 {} {}", idList, e);
            throw Exceptions.app(MessageConst.REMOVE_SECRET_KEY_ERROR, e);
        }
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取机器秘钥列表")
    public DataGrid<MachineSecretKeyVO> listKeys(@RequestBody MachineKeyRequest request) {
        return machineKeyService.listKeys(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取机器秘钥详情")
    public MachineSecretKeyVO getKeyDetail(@RequestBody MachineKeyRequest request) {
        Long id = Valid.notNull(request.getId());
        return machineKeyService.getKeyDetail(id);
    }

    @PostMapping("/mount")
    @ApiOperation(value = "挂载机器秘钥")
    @EventLog(EventType.MOUNT_MACHINE_KEY)
    public Map<String, Integer> mountKeys(@RequestBody MachineKeyRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        try {
            return machineKeyService.mountOrDumpKeys(idList, true);
        } catch (Exception e) {
            log.error("挂载秘钥失败 {} {}", idList, e);
            throw Exceptions.app(MessageConst.MOUNT_SECRET_KEY_ERROR, e);
        }
    }

    @PostMapping("/dump")
    @ApiOperation(value = "卸载机器秘钥")
    @EventLog(EventType.DUMP_MACHINE_KEY)
    public Map<String, Integer> dumpKeys(@RequestBody MachineKeyRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        try {
            return machineKeyService.mountOrDumpKeys(idList, false);
        } catch (Exception e) {
            log.error("卸载秘钥失败 {} {}", idList, e);
            throw Exceptions.app(MessageConst.DUMP_SECRET_KEY_ERROR, e);
        }
    }

    @GetMapping("/mount-all")
    @ApiOperation(value = "挂载所有机器秘钥")
    @EventLog(EventType.MOUNT_ALL_MACHINE_KEY)
    public void mountAllKey() {
        try {
            machineKeyService.mountAllKey();
        } catch (Exception e) {
            log.error("挂载所有秘钥失败", e);
            throw Exceptions.app(MessageConst.MOUNT_SECRET_KEY_ERROR, e);
        }
    }

    @GetMapping("/dump-all")
    @ApiOperation(value = "卸载所有机器秘钥")
    @EventLog(EventType.DUMP_ALL_MACHINE_KEY)
    public void dumpAllKey() {
        try {
            machineKeyService.dumpAllKey();
        } catch (Exception e) {
            log.error("卸载所有秘钥失败", e);
            throw Exceptions.app(MessageConst.DUMP_SECRET_KEY_ERROR, e);
        }
    }

    @PostMapping("/temp-mount")
    @ApiOperation(value = "临时挂载机器秘钥")
    @EventLog(EventType.TEMP_MOUNT_MACHINE_KEY)
    public Integer tempMount(@RequestBody MachineKeyRequest request) {
        String file = Valid.notBlank(request.getFile());
        String password = Valid.notBlank(request.getPassword());
        try {
            return machineKeyService.tempMountKey(file, password);
        } catch (Exception e) {
            log.error("临时挂载秘钥失败", e);
            throw Exceptions.app(MessageConst.TEMP_MOUNT_SECRET_KEY_ERROR, e);
        }
    }

}
