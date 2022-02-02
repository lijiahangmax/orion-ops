package com.orion.ops.controller;

import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.env.EnvViewType;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.entity.request.ApplicationEnvRequest;
import com.orion.ops.entity.vo.ApplicationEnvVO;
import com.orion.ops.service.api.ApplicationEnvService;
import com.orion.ops.utils.Valid;
import com.orion.utils.Exceptions;
import com.orion.utils.collect.Maps;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    @EventLog(EventType.DELETE_APP_ENV)
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
     * 应用环境变量列表
     */
    @RequestMapping("/list")
    public DataGrid<ApplicationEnvVO> listAppEnv(@RequestBody ApplicationEnvRequest request) {
        Valid.notNull(request.getAppId());
        Valid.notNull(request.getProfileId());
        return applicationEnvService.listAppEnv(request);
    }

    /**
     * 应用环境变量详情
     */
    @RequestMapping("/detail")
    public ApplicationEnvVO appEnvDetail(@RequestBody ApplicationEnvRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationEnvService.getAppEnvDetail(id);
    }

    /**
     * 同步应用环境变量到其他环境
     */
    @RequestMapping("/sync")
    @EventLog(EventType.SYNC_APP_ENV)
    public HttpWrapper<?> syncAppEnv(@RequestBody ApplicationEnvRequest request) {
        Long id = Valid.notNull(request.getId());
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        List<Long> targetProfileIdList = Valid.notEmpty(request.getTargetProfileIdList());
        applicationEnvService.syncAppEnv(id, appId, profileId, targetProfileIdList);
        return HttpWrapper.ok();
    }

    /**
     * 视图
     */
    @RequestMapping("/view")
    public String view(@RequestBody ApplicationEnvRequest request) {
        Valid.notNull(request.getAppId());
        Valid.notNull(request.getProfileId());
        EnvViewType viewType = Valid.notNull(EnvViewType.of(request.getViewType()));
        request.setLimit(Const.N_100000);
        // 查询列表
        Map<String, String> env = Maps.newLinkedMap();
        applicationEnvService.listAppEnv(request).forEach(e -> env.put(e.getKey(), e.getValue()));
        return viewType.toValue(env);
    }

    /**
     * 视图保存
     */
    @RequestMapping("/view/save")
    public Integer viewSave(@RequestBody ApplicationEnvRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        EnvViewType viewType = Valid.notNull(EnvViewType.of(request.getViewType()));
        String value = Valid.notBlank(request.getValue());
        try {
            MutableLinkedHashMap<String, String> result = viewType.toMap(value);
            applicationEnvService.saveEnv(appId, profileId, result);
            return result.size();
        } catch (Exception e) {
            throw Exceptions.argument(MessageConst.PARSE_ERROR, e);
        }
    }

}
