package com.orion.ops.controller;

import com.alibaba.fastjson.JSON;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.MachineKeyRequest;
import com.orion.ops.entity.vo.MachineSecretKeyVO;
import com.orion.ops.service.api.MachineKeyService;
import com.orion.ops.utils.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
        this.check(request);
        request.setId(null);
        try {
            Long id = machineKeyService.addUpdateSecretKey(request);
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
        this.check(request);
        Valid.notNull(request.getId());
        try {
            Long effect = machineKeyService.addUpdateSecretKey(request);
            return HttpWrapper.ok(effect.intValue());
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
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<MachineSecretKeyVO> listKeys(@RequestBody MachineKeyRequest request) {
        return machineKeyService.listKeys(request);
    }

    // 批量删除
    // 是否挂载
    // 下载
    // 重新挂载
    // 挂载
    // @RequestMapping("/use/machine")

    /**
     * 合法校验
     */
    private void check(MachineKeyRequest request) {
        Valid.notBlank(request.getName());
        Valid.notBlank(request.getFile());
    }

}
