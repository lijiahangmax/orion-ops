package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.EnvViewType;
import com.orion.ops.entity.request.MachineEnvRequest;
import com.orion.ops.entity.vo.MachineEnvVO;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 环境变量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 10:06
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/env")
public class MachineEnvController {

    @Resource
    private MachineEnvService machineEnvService;

    /**
     * 添加
     */
    @RequestMapping("/add")
    public Long add(@RequestBody MachineEnvRequest request) {
        Valid.notBlank(request.getKey());
        Valid.notNull(request.getValue());
        Valid.notNull(request.getMachineId());
        return machineEnvService.addEnv(request);
    }

    /**
     * 更新
     */
    @RequestMapping("/update")
    public Integer update(@RequestBody MachineEnvRequest request) {
        Valid.notNull(request.getId());
        Valid.notNull(request.getValue());
        return machineEnvService.updateEnv(request);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Integer delete(@RequestBody MachineEnvRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return machineEnvService.deleteEnv(idList);
    }

    // /**
    //  * 合并且替换
    //  */
    // @RequestMapping("/merge")
    // public Integer merge(@RequestBody MachineEnvRequest request) {
    //   Long sourceMachineId = Valid.notNull(request.getSourceMachineId());
    //   Long targetMachineId = Valid.notNull(request.getTargetMachineId());
    //   return machineEnvService.mergeEnv(sourceMachineId, targetMachineId);
    //   /**
    //    * 合并选择的目标id
    //    */
    //   private Long sourceMachineId;
    //
    //   /**
    //    * 合并选择的目标机器id
    //    */
    //   private Long targetMachineId;
    // }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<MachineEnvVO> list(@RequestBody MachineEnvRequest request) {
        Valid.notNull(request.getMachineId());
        return machineEnvService.listEnv(request);
    }

    /**
     * 视图
     */
    @RequestMapping("/view")
    public String view(@RequestBody MachineEnvRequest request) {
        Valid.notNull(request.getMachineId());
        EnvViewType viewType = Valid.notNull(EnvViewType.of(request.getViewType()));
        request.setLimit(Const.N_100000);
        // 查询列表
        Map<String, String> envs = machineEnvService.listEnv(request).stream()
                .collect(Collectors.toMap(MachineEnvVO::getKey, MachineEnvVO::getValue));
        return viewType.toValue(envs);
    }

}
