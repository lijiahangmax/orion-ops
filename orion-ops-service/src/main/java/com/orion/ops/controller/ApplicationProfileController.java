package com.orion.ops.controller;

import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.entity.request.ApplicationProfileRequest;
import com.orion.ops.entity.vo.ApplicationProfileVO;
import com.orion.ops.service.api.ApplicationProfileService;
import com.orion.ops.utils.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用环境api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 18:07
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/profile")
public class ApplicationProfileController {

    @Resource
    private ApplicationProfileService applicationProfileService;

    /**
     * 添加环境
     */
    @RequestMapping("/add")
    public Long addProfile(@RequestBody ApplicationProfileRequest request) {
        Valid.notBlank(request.getName());
        Valid.notBlank(request.getTag());
        Valid.in(request.getReleaseAudit(), Const.ENABLE, Const.DISABLE);
        return applicationProfileService.addProfile(request);
    }

    /**
     * 更新环境
     */
    @RequestMapping("/update")
    public Integer updateProfile(@RequestBody ApplicationProfileRequest request) {
        Valid.notNull(request.getId());
        Valid.notBlank(request.getName());
        Valid.notBlank(request.getTag());
        Valid.in(request.getReleaseAudit(), Const.ENABLE, Const.DISABLE);
        return applicationProfileService.updateProfile(request);
    }

    /**
     * 删除环境
     */
    @RequestMapping("/delete")
    public Integer deleteProfile(@RequestBody ApplicationProfileRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationProfileService.deleteProfile(id);
    }

    /**
     * 环境列表
     */
    @RequestMapping("/list")
    public List<ApplicationProfileVO> listProfile(@RequestBody ApplicationProfileRequest request) {
        return applicationProfileService.listProfile(request);
    }

}
