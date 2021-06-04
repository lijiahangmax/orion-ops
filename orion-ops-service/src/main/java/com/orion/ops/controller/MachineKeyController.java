package com.orion.ops.controller;

import com.alibaba.fastjson.JSON;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.IgnoreWrapper;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.RoleType;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.request.MachineKeyRequest;
import com.orion.ops.entity.vo.MachineSecretKeyVO;
import com.orion.ops.service.api.MachineKeyService;
import com.orion.ops.utils.Valid;
import com.orion.servlet.web.Servlets;
import com.orion.utils.io.FileReaders;
import com.orion.utils.io.Files1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
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
    public HttpWrapper<Long> addKey(@RequestBody MachineKeyRequest request) {
        Valid.notBlank(request.getName());
        Valid.notBlank(request.getFile());
        try {
            Long id = machineKeyService.addSecretKey(request);
            return HttpWrapper.ok(id);
        } catch (Exception e) {
            log.error("添加秘钥失败 {} {}", JSON.toJSONString(request), e);
            return HttpWrapper.error("添加秘钥失败");
        }
    }

    /**
     * 添加秘钥
     */
    @RequestMapping("/update")
    public HttpWrapper<Integer> updateKey(@RequestBody MachineKeyRequest request) {
        Valid.notBlank(request.getName());
        Valid.notNull(request.getId());
        try {
            Integer effect = machineKeyService.updateSecretKey(request);
            return HttpWrapper.ok(effect);
        } catch (Exception e) {
            log.error("修改秘钥失败 {} {}", JSON.toJSONString(request), e);
            return HttpWrapper.error("修改秘钥失败");
        }
    }

    /**
     * 删除秘钥
     */
    @RequestMapping("/remove")
    public HttpWrapper<Integer> removeKey(@RequestBody MachineKeyRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        try {
            Integer effect = machineKeyService.removeSecretKey(idList);
            return HttpWrapper.ok(effect);
        } catch (Exception e) {
            log.error("删除秘钥失败 {} {}", idList, e);
            return HttpWrapper.error("删除秘钥失败");
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
    public HttpWrapper<Integer> mountKey(@RequestBody MachineKeyRequest request) {
        Long id = Valid.notNull(request.getId());
        try {
            Integer status = machineKeyService.mountKey(id);
            return HttpWrapper.ok(status);
        } catch (Exception e) {
            log.error("挂载秘钥失败 {} {}", id, e);
            return HttpWrapper.error("挂载秘钥失败");
        }
    }

    /**
     * 卸载秘钥
     */
    @RequestMapping("/unmount")
    public HttpWrapper<Integer> unmountKey(@RequestBody MachineKeyRequest request) {
        Long id = Valid.notNull(request.getId());
        try {
            Integer status = machineKeyService.unmountKey(id);
            return HttpWrapper.ok(status);
        } catch (Exception e) {
            log.error("挂载秘钥失败 {} {}", id, e);
            return HttpWrapper.error("卸载秘钥失败");
        }
    }

    /**
     * 下载
     */
    @RequestMapping("/download/{id}")
    @IgnoreWrapper
    @RequireRole(value = {RoleType.SUPER_ADMINISTRATOR, RoleType.ADMINISTRATOR})
    public void download(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        Valid.notNull(id);
        MachineSecretKeyDO key = machineKeyService.getKeyById(id);
        if (key == null) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=notfound_id_rsa");
            return;
        }
        File file = new File(MachineKeyService.getKeyPath(key.getSecretKeyPath()));
        if (!Files1.isFile(file)) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=notfound_id_rsa");
            return;
        }
        Servlets.transfer(response, FileReaders.readFast(file), Files1.getFileName(file));
    }

    /**
     * 临时挂载秘钥
     */
    @RequestMapping("/temp-mount")
    public HttpWrapper<Integer> tempMount(@RequestBody MachineKeyRequest request) {
        String file = Valid.notBlank(request.getFile());
        try {
            Integer status = machineKeyService.tempMountKey(file, request.getPassword());
            return HttpWrapper.ok(status);
        } catch (Exception e) {
            log.error("临时挂载秘钥失败", e);
            return HttpWrapper.error("临时挂载秘钥失败");
        }
    }

}
