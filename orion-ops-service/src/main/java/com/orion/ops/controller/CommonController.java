package com.orion.ops.controller;

import com.alibaba.fastjson.JSON;
import com.orion.ops.OrionOpsServiceApplication;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.service.api.CommonService;
import com.orion.ops.utils.Currents;
import com.orion.utils.Exceptions;
import com.orion.utils.io.StreamReaders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 公共 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/10 17:37
 */
@Api(tags = "公共接口")
@RestController
@RestWrapper
@RequestMapping("/orion/api/common")
public class CommonController {

    @Resource
    private CommonService commonService;

    @PostMapping("/menu")
    @ApiOperation(value = "获取菜单")
    public List<?> getMenu() throws IOException {
        UserDTO user = Currents.getUser();
        String menuFile;
        if (RoleType.ADMINISTRATOR.getType().equals(user.getRoleType())) {
            menuFile = "menu-admin.json";
        } else if (RoleType.DEVELOPER.getType().equals(user.getRoleType())) {
            menuFile = "menu-dev.json";
        } else if (RoleType.OPERATION.getType().equals(user.getRoleType())) {
            menuFile = "menu-opt.json";
        } else {
            throw Exceptions.app();
        }
        InputStream menu = OrionOpsServiceApplication.class.getResourceAsStream("/menu/" + menuFile);
        return JSON.parseArray(new String(StreamReaders.readAllBytes(menu)));
    }

}
