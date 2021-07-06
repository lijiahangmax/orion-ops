package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.entity.request.ApplicationEnvRequest;
import com.orion.ops.entity.vo.ApplicationEnvVO;
import com.orion.ops.service.api.ApplicationEnvService;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用环境 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/4 11:10
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-env")
public class ApplicationEnvController {

    @Resource
    private ApplicationEnvService applicationEnvService;

    // merge diff

    /**
     * 添加应用变量
     */
    @RequestMapping("/add")
    public Long addAppEnv(@RequestBody ApplicationEnvRequest request) {
        Valid.notNull(request.getAppId());
        Valid.notNull(request.getProfileId());
        Valid.notBlank(request.getKey());
        Valid.notBlank(request.getValue());
        return applicationEnvService.addAppEnv(request);
    }

    /**
     * 删除应用变量
     */
    @RequestMapping("/delete")
    public Integer deleteAppEnv(@RequestBody ApplicationEnvRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return applicationEnvService.deleteAppEnv(idList);
    }

    /**
     * 更新应用变量
     */
    @RequestMapping("/update")
    public Integer updateAppEnv(@RequestBody ApplicationEnvRequest request) {
        Valid.notNull(request.getId());
        return applicationEnvService.updateAppEnv(request);
    }

    /**
     * 应用环境列表
     */
    @RequestMapping("/list")
    public DataGrid<ApplicationEnvVO> listAppEnv(@RequestBody ApplicationEnvRequest request) {
        Valid.notNull(request.getAppId());
        Valid.notNull(request.getProfileId());
        return applicationEnvService.listAppEnv(request);
    }

}
