package com.orion.ops.controller;

import com.alibaba.fastjson.JSON;
import com.orion.id.ObjectIds;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.lang.wrapper.PageRequest;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.MachineEnvAttr;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.request.MachineKeyRequest;
import com.orion.ops.entity.vo.MachineSecretKeyVO;
import com.orion.ops.service.api.MachineKeyService;
import com.orion.ops.utils.Valid;
import com.orion.utils.io.Files1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

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
     * 添加秘钥k
     */
    @RequestMapping("/add")
    public HttpWrapper<Long> addKey(@RequestParam("file") MultipartFile file,
                                    @RequestParam("name") String name, @RequestParam(value = "password", required = false) String password,
                                    @RequestParam(value = "description", required = false) String description) throws IOException {
        MachineSecretKeyDO key = new MachineSecretKeyDO();
        key.setKeyName(name);
        key.setPassword(password);
        key.setDescription(description);
        String path = Files1.getPath(MachineEnvAttr.KEY_PATH.getValue() + "/" + ObjectIds.next() + "_id_rsa");
        Files1.touch(path);
        file.transferTo(new File(path));
        key.setSecretKeyPath(path);
        try {
            Long id = machineKeyService.addSecretKey(key);
            return HttpWrapper.ok(id);
        } catch (Exception e) {
            log.error("添加秘钥失败 {} {}", JSON.toJSONString(key), e);
            return HttpWrapper.error("添加秘钥失败");
        }
    }

    /**
     * 删除秘钥
     */
    @RequestMapping("/remove")
    public HttpWrapper<Integer> removeKey(@RequestBody MachineKeyRequest request) {
        Long id = Valid.notNull(request.getId());
        try {
            Integer effect = machineKeyService.removeSecretKey(id);
            return HttpWrapper.ok(effect);
        } catch (Exception e) {
            log.error("删除秘钥失败 {} {}", id, e);
            return HttpWrapper.error("删除秘钥失败");
        }
    }

    /**
     * 添加秘钥
     */
    @RequestMapping("/update")
    public HttpWrapper<Integer> updateKey(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id,
                                          @RequestParam("name") String name, @RequestParam(value = "password", required = false) String password,
                                          @RequestParam(value = "description", required = false) String description) throws IOException {
        MachineSecretKeyDO key = new MachineSecretKeyDO();
        key.setId(id);
        key.setKeyName(name);
        key.setPassword(password);
        key.setDescription(description);
        String path = Files1.getPath(MachineEnvAttr.KEY_PATH.getValue() + "/" + ObjectIds.next() + "_id_rsa");
        file.transferTo(new File(path));
        key.setSecretKeyPath(path);
        try {
            Integer effect = machineKeyService.updateSecretKey(key);
            return HttpWrapper.ok(effect);
        } catch (Exception e) {
            log.error("修改秘钥失败 {} {}", JSON.toJSONString(key), e);
            return HttpWrapper.error("修改秘钥失败");
        }
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<MachineSecretKeyVO> listKeys(@RequestBody PageRequest page) {
        return machineKeyService.listKeys(page);
    }

    // @RequestMapping("/use/machine")

}
