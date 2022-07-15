package com.orion.ops.controller;

import com.orion.lang.define.collect.MutableLinkedHashMap;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.collect.Maps;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.env.EnvViewType;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.SystemEnvRequest;
import com.orion.ops.entity.vo.SystemEnvVO;
import com.orion.ops.service.api.SystemEnvService;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 系统环境变量 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 20:40
 */
@Api(tags = "系统环境变量")
@RestController
@RestWrapper
@RequestMapping("/orion/api/system-env")
public class SystemEnvController {

    @Resource
    private SystemEnvService systemEnvService;

    @PostMapping("/add")
    @ApiOperation(value = "添加环境变量")
    @EventLog(EventType.ADD_SYSTEM_ENV)
    public Long add(@RequestBody SystemEnvRequest request) {
        Valid.notBlank(request.getKey());
        Valid.notNull(request.getValue());
        return systemEnvService.addEnv(request);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改环境变量")
    @EventLog(EventType.UPDATE_SYSTEM_ENV)
    public Integer update(@RequestBody SystemEnvRequest request) {
        Valid.notNull(request.getId());
        Valid.notNull(request.getValue());
        return systemEnvService.updateEnv(request);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除环境变量")
    @EventLog(EventType.DELETE_SYSTEM_ENV)
    public Integer delete(@RequestBody SystemEnvRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return systemEnvService.deleteEnv(idList);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取环境变量列表")
    public DataGrid<SystemEnvVO> list(@RequestBody SystemEnvRequest request) {
        return systemEnvService.listEnv(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取环境变量详情")
    public SystemEnvVO detail(@RequestBody SystemEnvRequest request) {
        Long id = Valid.notNull(request.getId());
        return systemEnvService.getEnvDetail(id);
    }

    @PostMapping("/view")
    @ApiOperation(value = "获取环境变量视图")
    public String view(@RequestBody SystemEnvRequest request) {
        EnvViewType viewType = Valid.notNull(EnvViewType.of(request.getViewType()));
        request.setLimit(Const.N_100000);
        // 查询列表
        Map<String, String> env = Maps.newLinkedMap();
        systemEnvService.listEnv(request).forEach(e -> env.put(e.getKey(), e.getValue()));
        return viewType.toValue(env);
    }

    @PostMapping("/view-save")
    @ApiOperation(value = "保存环境变量视图")
    @EventLog(EventType.SAVE_SYSTEM_ENV)
    public Integer viewSave(@RequestBody SystemEnvRequest request) {
        String value = Valid.notBlank(request.getValue());
        EnvViewType viewType = Valid.notNull(EnvViewType.of(request.getViewType()));
        try {
            MutableLinkedHashMap<String, String> result = viewType.toMap(value);
            systemEnvService.saveEnv(result);
            return result.size();
        } catch (Exception e) {
            throw Exceptions.argument(MessageConst.PARSE_ERROR, e);
        }
    }

}
