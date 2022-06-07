package com.orion.ops.controller;

import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.entity.request.ApplicationProfileRequest;
import com.orion.ops.entity.vo.ApplicationProfileFastVO;
import com.orion.ops.entity.vo.ApplicationProfileVO;
import com.orion.ops.service.api.ApplicationProfileService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
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
 * @since 2021/7/2 18:07
 */
@Api(tags = "应用环境")
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-profile")
public class ApplicationProfileController {

    @Resource
    private ApplicationProfileService applicationProfileService;

    @PostMapping("/add")
    @ApiOperation(value = "添加应用环境")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.ADD_PROFILE)
    public Long addProfile(@RequestBody ApplicationProfileRequest request) {
        Valid.notBlank(request.getName());
        Valid.notBlank(request.getTag());
        Valid.in(request.getReleaseAudit(), Const.ENABLE, Const.DISABLE);
        return applicationProfileService.addProfile(request);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新应用环境")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.UPDATE_PROFILE)
    public Integer updateProfile(@RequestBody ApplicationProfileRequest request) {
        Valid.notNull(request.getId());
        if (request.getReleaseAudit() != null) {
            Valid.in(request.getReleaseAudit(), Const.ENABLE, Const.DISABLE);
        }
        return applicationProfileService.updateProfile(request);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除应用环境")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.DELETE_PROFILE)
    public Integer deleteProfile(@RequestBody ApplicationProfileRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationProfileService.deleteProfile(id);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取应用环境列表")
    public List<ApplicationProfileVO> listProfiles(@RequestBody ApplicationProfileRequest request) {
        return applicationProfileService.listProfiles(request);
    }

    @PostMapping("/fast-list")
    @ApiOperation(value = "获取应用环境列表 (缓存)")
    public List<ApplicationProfileFastVO> listProfiles() {
        return applicationProfileService.fastListProfiles();
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取应用环境详情")
    public ApplicationProfileVO getProfile(@RequestBody ApplicationProfileRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationProfileService.getProfile(id);
    }

}
