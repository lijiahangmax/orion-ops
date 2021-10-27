package com.orion.ops.controller;

import com.alibaba.fastjson.JSON;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.entity.request.MachineKeyRequest;
import com.orion.ops.entity.vo.MachineSecretKeyVO;
import com.orion.ops.service.api.MachineKeyService;
import com.orion.ops.utils.Valid;
import com.orion.utils.Exceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 机器秘钥
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 23:10
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/key")
@Slf4j
public class MachineKeyController {

    @Resource
    private MachineKeyService machineKeyService;

    /**
     * 添加秘钥
     */
    @RequestMapping("/add")
    public Long addKey(@RequestBody MachineKeyRequest request) {
        Valid.notBlank(request.getName());
        Valid.notBlank(request.getPassword());
        Valid.notBlank(request.getFile());
        try {
            return machineKeyService.addSecretKey(request);
        } catch (Exception e) {
            log.error("添加秘钥失败 {} {}", JSON.toJSONString(request), e);
            throw Exceptions.runtime(MessageConst.ADD_SECRET_KEY_ERROR, e);
        }
    }

    /**
     * 添加秘钥
     */
    @RequestMapping("/update")
    public Integer updateKey(@RequestBody MachineKeyRequest request) {
        Valid.notNull(request.getId());
        try {
            return machineKeyService.updateSecretKey(request);
        } catch (Exception e) {
            log.error("修改秘钥失败 {} {}", JSON.toJSONString(request), e);
            throw Exceptions.runtime(MessageConst.UPDATE_SECRET_KEY_ERROR, e);
        }
    }

    /**
     * 删除秘钥
     */
    @RequestMapping("/remove")
    public Integer removeKey(@RequestBody MachineKeyRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        try {
            return machineKeyService.removeSecretKey(idList);
        } catch (Exception e) {
            log.error("删除秘钥失败 {} {}", idList, e);
            throw Exceptions.runtime(MessageConst.REMOVE_SECRET_KEY_ERROR, e);
        }
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<MachineSecretKeyVO> listKeys(@RequestBody MachineKeyRequest request) {
        return machineKeyService.listKeys(request);
    }

    /**
     * 挂载秘钥
     */
    @RequestMapping("/mount")
    public Integer mountKey(@RequestBody MachineKeyRequest request) {
        Long id = Valid.notNull(request.getId());
        try {
            return machineKeyService.mountKey(id);
        } catch (Exception e) {
            log.error("挂载秘钥失败 {} {}", id, e);
            throw Exceptions.runtime(MessageConst.MOUNT_SECRET_KEY_ERROR, e);
        }
    }

    /**
     * 卸载秘钥
     */
    @RequestMapping("/dump")
    public Integer dumpKey(@RequestBody MachineKeyRequest request) {
        Long id = Valid.notNull(request.getId());
        try {
            return machineKeyService.dumpKey(id);
        } catch (Exception e) {
            log.error("卸载秘钥失败 {} {}", id, e);
            throw Exceptions.runtime(MessageConst.DUMP_SECRET_KEY_ERROR, e);
        }
    }

    /**
     * 挂载所有秘钥
     */
    @RequestMapping("/mount-all")
    public void mountAllKey() {
        try {
            machineKeyService.mountAllKey();
        } catch (Exception e) {
            log.error("挂载所有秘钥失败", e);
            throw Exceptions.runtime(MessageConst.MOUNT_SECRET_KEY_ERROR, e);
        }
    }

    /**
     * 卸载所有秘钥
     */
    @RequestMapping("/dump-all")
    public void dumpAllKey() {
        try {
            machineKeyService.dumpAllKey();
        } catch (Exception e) {
            log.error("卸载所有秘钥失败", e);
            throw Exceptions.runtime(MessageConst.DUMP_SECRET_KEY_ERROR, e);
        }
    }

    /**
     * 临时挂载秘钥
     */
    @RequestMapping("/temp-mount")
    public Integer tempMount(@RequestBody MachineKeyRequest request) {
        String file = Valid.notBlank(request.getFile());
        String password = Valid.notBlank(request.getPassword());
        try {
            return machineKeyService.tempMountKey(file, password);
        } catch (Exception e) {
            log.error("临时挂载秘钥失败", e);
            throw Exceptions.runtime(MessageConst.TEMP_MOUNT_SECRET_KEY_ERROR, e);
        }
    }

}
