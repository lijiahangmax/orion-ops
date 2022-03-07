package com.orion.ops.controller;

import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.env.EnvViewType;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.entity.request.SystemEnvRequest;
import com.orion.ops.entity.vo.SystemEnvVO;
import com.orion.ops.service.api.SystemEnvService;
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
 * 系统环境变量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 20:40
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/system-env")
public class SystemEnvController {

    @Resource
    private SystemEnvService systemEnvService;

    /**
     * 添加
     */
    @RequestMapping("/add")
    @EventLog(EventType.ADD_SYSTEM_ENV)
    public Long add(@RequestBody SystemEnvRequest request) {
        Valid.notBlank(request.getKey());
        Valid.notNull(request.getValue());
        return systemEnvService.addEnv(request);
    }

    /**
     * 更新
     */
    @RequestMapping("/update")
    @EventLog(EventType.UPDATE_SYSTEM_ENV)
    public Integer update(@RequestBody SystemEnvRequest request) {
        Valid.notNull(request.getId());
        Valid.notNull(request.getValue());
        return systemEnvService.updateEnv(request);
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @EventLog(EventType.DELETE_SYSTEM_ENV)
    public Integer delete(@RequestBody SystemEnvRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return systemEnvService.deleteEnv(idList);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public DataGrid<SystemEnvVO> list(@RequestBody SystemEnvRequest request) {
        return systemEnvService.listEnv(request);
    }

    /**
     * 详情
     */
    @RequestMapping("/detail")
    public SystemEnvVO detail(@RequestBody SystemEnvRequest request) {
        Long id = Valid.notNull(request.getId());
        return systemEnvService.getEnvDetail(id);
    }

    /**
     * 视图
     */
    @RequestMapping("/view")
    public String view(@RequestBody SystemEnvRequest request) {
        EnvViewType viewType = Valid.notNull(EnvViewType.of(request.getViewType()));
        request.setLimit(Const.N_100000);
        // 查询列表
        Map<String, String> env = Maps.newLinkedMap();
        systemEnvService.listEnv(request).forEach(e -> env.put(e.getKey(), e.getValue()));
        return viewType.toValue(env);
    }

    /**
     * 视图保存
     */
    @RequestMapping("/view-save")
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
