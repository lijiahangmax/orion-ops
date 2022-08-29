package com.orion.ops.expose;

import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.vo.machine.MachineAlarmConfigWrapperVO;
import com.orion.ops.service.api.MachineAlarmConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 机器报警配置 暴露api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/29 16:32
 */
@Api(tags = "暴露-机器报警配置")
@RestController
@RestWrapper
@RequestMapping("/orion/expose-api/machine-alarm-config")
public class MachineAlarmConfigExposeController {

    @Resource
    private MachineAlarmConfigService machineAlarmConfigService;

    @GetMapping("/get")
    @ApiOperation(value = "获取报警配置")
    public MachineAlarmConfigWrapperVO getAlarmConfig(@RequestParam("machineId") Long machineId) {
        return machineAlarmConfigService.getAlarmConfig(machineId);
    }

}
