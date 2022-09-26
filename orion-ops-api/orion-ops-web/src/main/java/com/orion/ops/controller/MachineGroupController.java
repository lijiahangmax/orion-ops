package com.orion.ops.controller;

import com.orion.ops.annotation.RestWrapper;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
